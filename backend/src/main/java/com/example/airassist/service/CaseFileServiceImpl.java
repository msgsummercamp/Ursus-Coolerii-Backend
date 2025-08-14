package com.example.airassist.service;

import com.example.airassist.common.dto.*;
import com.example.airassist.common.enums.CaseStatus;
import com.example.airassist.common.enums.DaysBeforeNotice;
import com.example.airassist.common.enums.HoursBeforeArrival;
import com.example.airassist.common.exceptions.UserNotFoundException;
import com.example.airassist.persistence.dao.CaseFileRepository;
import com.example.airassist.persistence.dao.CaseFlightRepository;
import com.example.airassist.persistence.dao.PassengerRepository;
import com.example.airassist.persistence.model.*;
import com.example.airassist.redis.Airport;
import com.example.airassist.util.DtoUtils;
import com.example.airassist.util.PdfGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
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
    private final MailSenderService mailSenderService;

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
                               FlightService flightService,
                               MailSenderService mailSenderService) {
        this.caseFileRepository = caseFileRepository;
        this.airportService = airportService;
        this.userService = userService;
        this.airlineService = airlineService;
        this.passengerRepository = passengerRepository;
        this.caseFlightRepository = caseFlightRepository;
        this.flightService = flightService;
        this.mailSenderService = mailSenderService;
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
    public CaseFile saveCase(CaseRequest saveRequest, List<MultipartFile> uploadedDocuments) {
        User creatorUser = userService.findByEmail(saveRequest.getUserEmail()).orElseThrow(() ->
                new UserNotFoundException("User with email " + saveRequest.getUserEmail() + " not found", HttpStatus.NOT_FOUND));
        Passenger passenger = saveRequest.getPassenger();
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        CaseFile caseFileToSave = CaseFile.builder()
                .contractId(generateContractId(currentTime))
                .passenger(passenger)
                .reservationNumber(saveRequest.getReservationNumber())
                .user(creatorUser)
                .disruptionDetails(saveRequest.getDisruptionDetails())
                .status(CaseStatus.NOT_ASSIGNED)
                .caseDate(currentTime)
                .build();

        List<Document> documents = toDocument(uploadedDocuments);
        CaseFile finalCaseFileToSave1 = caseFileToSave;
        documents.forEach(document -> document.setCaseFile(finalCaseFileToSave1));
        caseFileToSave.setDocuments(documents);
        caseFileToSave = caseFileRepository.save(caseFileToSave);

        List<CaseFlights> caseFlights = getCaseFlights(caseFileToSave, saveRequest.getFlights());
        CaseFile finalCaseFileToSave = caseFileToSave;

        caseFlights.forEach(c -> {
            c.setCaseFile(finalCaseFileToSave);
            c.setId(new CaseFlightsId(c.getCaseFile().getCaseId(), c.getFlight().getFlightId()));
        });
        caseFlightRepository.saveAll(caseFlights);
        caseFileToSave.setCaseFlights(caseFlights);

        byte[] pdfBytes;
        try {
            LocalDate caseDateOnly = caseFileToSave.getCaseDate().toLocalDateTime().toLocalDate();
            DateTimeFormatter europeanFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String formattedDate = caseDateOnly.format(europeanFormat);
            pdfBytes = PdfGenerator.generateCasePdf(
                    caseFileToSave.getContractId(),
                    formattedDate,
                    passenger.getFirstName(),
                    passenger.getLastName(),
                    caseFileToSave.getReservationNumber()
            );
        } catch (IOException e) {
            log.error("Error generating PDF for case file: ", e);
            throw new RuntimeException("Failed to generate PDF for case file", e);
        }

        mailSenderService.sendMailWithCaseAndPdf(saveRequest.getUserEmail(), caseFileToSave.getContractId(), pdfBytes);

        return caseFileToSave;
    }

    private Document toDocument(MultipartFile file) {
        try {
            return  Document.builder().content(file.getBytes()).build();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file: " + file.getOriginalFilename(), e);
        }
    }

    private List<Document> toDocument(List<MultipartFile> files){
        return files.stream()
                .map(this::toDocument)
                .toList();
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
                return isEligibleForDelay(eligibilityRequest.getDelayHours());
            }
            case DENIED_BOARDING -> {
                return isEligibleForDeniedBoarding(eligibilityRequest.getIsVoluntarilyGivenUp());
            }
            default -> {
                return false;
            }
        }
    }

    private boolean isEligibleForCancellation(DaysBeforeNotice noticeDays) {
        return noticeDays == DaysBeforeNotice.LESS_THAN_14 || noticeDays == DaysBeforeNotice.ON_FLIGHT_DAY;
    }

    private boolean isEligibleForDelay(HoursBeforeArrival delayHours) {
        return delayHours == HoursBeforeArrival.MORE_THAN_3 || delayHours == HoursBeforeArrival.LOST_CONNECTION;
    }

    private boolean isEligibleForDeniedBoarding( Boolean isVoluntarilyGivenUp) {
        return isVoluntarilyGivenUp != null && !isVoluntarilyGivenUp;
    }

    @Override
    public Page<CaseFileSummaryDTO> getCaseSummariesByPassengerId(UUID passengerId, Pageable pageable) {
        Page<CaseFile> cases = caseFileRepository.findByUserId(passengerId, pageable);
        return cases.map(this::mapCaseFileToDTO);
    }

    @Override
    public CaseFile findCaseFileById(UUID caseId) {
        return caseFileRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Case not found"));
    }

    @Override
    public Page<CaseFileSummaryDTO> findAll(Pageable pageable) {
        log.info("Finding all cases");
        Page<CaseFile> cases = caseFileRepository.findAll(pageable);
        return cases.map(this::mapCaseFileToDTO);
    }

    private String generateContractId(Timestamp caseDate) {
        String timestamp = String.valueOf(caseDate.getTime());
        return timestamp.substring(Math.max(0, timestamp.length() - 6));
    }

    private CaseFileSummaryDTO mapCaseFileToDTO(CaseFile caseFile) {
        CaseFileSummaryDTO caseFileSummaryDTO = new CaseFileSummaryDTO();
        caseFileSummaryDTO.setCaseId(caseFile.getCaseId());
        caseFileSummaryDTO.setContractId(caseFile.getContractId());
        caseFileSummaryDTO.setCaseDate(caseFile.getCaseDate());
        CaseFlights caseFlight = caseFile.getCaseFlights().stream().filter(CaseFlights::isProblemFlight).findFirst().orElse(null);
        if (caseFlight != null) {
            caseFileSummaryDTO.setFlightNr(caseFlight.getFlight().getFlightNumber());
            caseFileSummaryDTO.setFlightDepartureDate(caseFlight.getFlight().getDepartureTime());
            caseFileSummaryDTO.setFlightArrivalDate(caseFlight.getFlight().getArrivalTime());
        }
        Passenger passenger = caseFile.getPassenger();
        String passengerName = (passenger.getFirstName() != null ? passenger.getFirstName() : "") +
                " " +
                (passenger.getLastName() != null ? passenger.getLastName() : "");
        caseFileSummaryDTO.setReservationNumber(caseFile.getReservationNumber());
        caseFileSummaryDTO.setPassengerName(passengerName.trim());
        caseFileSummaryDTO.setStatus(caseFile.getStatus());
        User employee = caseFile.getEmployee();
        String employeeName = (employee != null ?
                ((employee.getFirstName() != null ? employee.getFirstName() : "") +
                        " " +
                        (employee.getLastName() != null ? employee.getLastName() : "")).trim()
                : null);
        caseFileSummaryDTO.setColleague(employeeName);
        return caseFileSummaryDTO;
    }

    @Override
    public CaseDetailsDTO getCaseDetailsByCaseId(UUID caseId) {
        CaseFile caseFile = caseFileRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Case not found"));
        return DtoUtils.getCaseDetailsDtoFromCaseFile(caseFile);
    }

}
