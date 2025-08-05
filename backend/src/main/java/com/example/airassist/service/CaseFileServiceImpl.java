package com.example.airassist.service;

import com.example.airassist.common.dto.CalculateRewardRequest;
import com.example.airassist.common.dto.EligibilityRequest;
import com.example.airassist.common.dto.FlightSaveDTO;
import com.example.airassist.common.dto.SaveCaseRequest;
import com.example.airassist.common.enums.CaseStatus;
import com.example.airassist.common.exceptions.UserNotFoundException;
import com.example.airassist.persistence.dao.CaseFileRepository;
import com.example.airassist.persistence.dao.CaseFlightRepository;
import com.example.airassist.persistence.dao.PassengerRepository;
import com.example.airassist.persistence.model.*;
import com.example.airassist.redis.Airport;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CaseFileServiceImpl implements CaseFileService {

    private final CaseFileRepository caseFileRepository;
    private final PassengerRepository passengerRepository;
    private final AirportService airportService;
    private final UserService userService;
    private final AirlineService airlineService;
    private final CaseFlightRepository caseFlightRepository;
    private final FlightService flightService;

    @Value("${MIN_REWARD}")
    private int minReward;

    @Value("${MED_REWARD}")
    private int medReward;

    @Value("${MAX_REWARD}")
    private int maxReward;

    @Value("${LOW_DISTANCE_THRESHOLD}")
    private int lowDistanceThreshold;

    @Value("${HIGH_DISTANCE_THRESHOLD}")
    private int highDistanceThreshold;

    public CaseFileServiceImpl(CaseFileRepository caseFileRepository,
                               AirportService airportService,
                               UserService userService,
                               AirlineService airlineService,
                               PassengerRepository passengerRepository,
                               CaseFlightRepository caseFlightRepository,
                               FlightService flightService) {
        this.caseFileRepository = caseFileRepository;
        this.airportService = airportService;
        this.userService = userService;
        this.airlineService = airlineService;
        this.passengerRepository = passengerRepository;
        this.caseFlightRepository = caseFlightRepository;
        this.flightService = flightService;
    }

    @Override
    public List<CaseFile> findAllCaseFiles() {
        return caseFileRepository.findAll();
    }

    @Override
    public int calculateCaseReward(CalculateRewardRequest calculateRewardRequest) {
        if (calculateRewardRequest == null || calculateRewardRequest.getDepartureAirport() == null || calculateRewardRequest.getDestinationAirport() == null) {
            log.warn("CaseFile or its flights are null or empty");
            return 0;
        }
/// Make a function in service that retrevies an aiport byName unnecessary complicated code
        Airport departureAirport = airportService.getAllAirports().stream()
                .filter(airport -> airport.getName().equals(calculateRewardRequest.getDepartureAirport()))
                .findFirst().orElse(null);
        Airport destinationAirport = airportService.getAllAirports().stream()
                .filter(airport -> airport.getName().equals(calculateRewardRequest.getDestinationAirport()))
                .findFirst().orElse(null);

        if(departureAirport == null || destinationAirport == null ||
                departureAirport.getIata() == null || destinationAirport.getIata() == null) {
            log.warn("Departure or destination airport not found in Redis");
            return 0;
        }
        double distance = calculateDistance(departureAirport.getIata(), destinationAirport.getIata());

        if (distance < lowDistanceThreshold)
            return minReward;
        else if (distance < highDistanceThreshold)
            return medReward;
        else return maxReward;
    }

    @Override
    @Transactional
    public CaseFile saveCase(SaveCaseRequest saveCaseRequest) {
        User creatorUser = userService.findByEmail(saveCaseRequest.getUserEmail()).orElseThrow(() ->
                new UserNotFoundException("User with email " + saveCaseRequest.getUserEmail() + " not found", HttpStatus.NOT_FOUND));
        Passenger passenger = passengerRepository.save(saveCaseRequest.getPassenger());
        CaseFile caseFileToSave = CaseFile.builder()
                .passenger(passenger)
                .reservationNumber(saveCaseRequest.getReservationNumber())
                .user(creatorUser)
                .disruptionDetails(saveCaseRequest.getDisruptionDetails())
                .status(CaseStatus.NOT_ASSIGNED)
                .build();

        caseFileToSave = caseFileRepository.save(caseFileToSave);

        List<CaseFlights> caseFlights = getCaseFlights(caseFileToSave, saveCaseRequest.getFlights());
        CaseFile finalCaseFileToSave = caseFileToSave;

        caseFlights.forEach(c -> {
            c.setCaseFile(finalCaseFileToSave);
            c.setId(new CaseFlightsId(c.getCaseFile().getCaseId(), c.getFlight().getFlightId()));
        });
        caseFlightRepository.saveAll(caseFlights);
        caseFileToSave.setCaseFlights(caseFlights);


        return caseFileToSave;
    }

    private List<CaseFlights> getCaseFlights(CaseFile caseFileToSave, List<FlightSaveDTO> flightsFromRequest) {
        List<Flight> flights = flightService.saveAll(getFlightsFromDTO(flightsFromRequest));
        return java.util.stream.IntStream.range(0, flightsFromRequest.size())
                .mapToObj(i -> CaseFlights.builder()
                        .caseFile(caseFileToSave)
                        .flight(flights.get(i))
                        .isProblemFlight(flightsFromRequest.get(i).isProblemFlight())
                        .isLast(flightsFromRequest.get(i).isLastFlight())
                        .isFirst(flightsFromRequest.get(i).isFirstFlight())
                        .build())
                .collect(Collectors.toList());
    }

    private List<Flight> getFlightsFromDTO(List<FlightSaveDTO> flights) {
        return flights.stream()
                .map(f -> Flight.builder()
                        .flightNumber(f.getFlightNumber())
                        .departureAirport(f.getDepartureAirport())
                        .airline(getAirlineFromAirlineName(f.getAirlineName()))
                        .destinationAirport(f.getDestinationAirport())
                        .departureTime(f.getDepartureTime())
                        .arrivalTime(f.getArrivalTime())
                        .build())
                .toList();
    }

    private Airline getAirlineFromAirlineName(String airlineName) {
        return airlineService.getAirlineByName(airlineName);
    }

    private double calculateDistance(String departureAirport, String destinationAirport) {
        try {
            String body = "from=" + departureAirport + "&to=" + destinationAirport;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://airportgap.com/api/airports/distance"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("API error: " + response.statusCode());
            }

            ObjectMapper mapper = new ObjectMapper();
            return mapper.readTree(response.body())
                    .path("data")
                    .path("attributes")
                    .path("kilometers")
                    .asDouble(0);

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public Boolean isEligible(EligibilityRequest eligibilityRequest) {
        if (eligibilityRequest == null || eligibilityRequest.getDisruption() == null) {
            return false;
        }

        switch (eligibilityRequest.getDisruption()) {
            case CANCELLATION -> {
                return isEligibleForCancellation(eligibilityRequest.getNoticeDays());
            }
            case DELAY -> {
                return isEligibleForDelay(eligibilityRequest.getDelayHours(), eligibilityRequest.getArrived());
            }
            case DENIED_BOARDING -> {
                return isEligibleForDeniedBoarding(eligibilityRequest.getIsVoluntarilyGivenUp());
            }
            default -> {
                return false;
            }
        }
    }

    private boolean isEligibleForCancellation( Integer noticeDays) {
        return noticeDays != null && noticeDays < 14;
    }

    private boolean isEligibleForDelay(Integer delayHours, Boolean arrived) {
        return (arrived != null && !arrived) || (delayHours != null && delayHours > 3);
    }

    private boolean isEligibleForDeniedBoarding( Boolean isVoluntarilyGivenUp) {
        return isVoluntarilyGivenUp != null && !isVoluntarilyGivenUp;
    }
}
