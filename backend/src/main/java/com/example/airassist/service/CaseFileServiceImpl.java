package com.example.airassist.service;

import com.example.airassist.common.dto.CalculateRewardRequest;
import com.example.airassist.common.dto.EligibilityRequest;
import com.example.airassist.persistence.dao.CaseFileRepository;
import com.example.airassist.persistence.model.CaseFile;
import com.example.airassist.redis.Airport;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class CaseFileServiceImpl implements CaseFileService {

    private final CaseFileRepository caseFileRepository;
    private final int MIN_REWARD = 250;
    private final int MED_REWARD = 400;
    private final int MAX_REWARD = 600;
    private final int LOW_DISTANCE_THRESHOLD = 1500;
    private final int HIGH_DISTANCE_THRESHOLD = 3000;

    private final AirportService airportService;

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

        if (distance < LOW_DISTANCE_THRESHOLD)
            return MIN_REWARD;
        else if (distance < HIGH_DISTANCE_THRESHOLD)
            return MED_REWARD;
        else return MAX_REWARD;
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
        return noticeDays < 14;
    }

    private boolean isEligibleForDelay(Integer delayHours, Boolean arrived) {
        return !arrived || delayHours > 3;
    }

    private boolean isEligibleForDeniedBoarding( Boolean isVoluntarilyGivenUp) {
        return !isVoluntarilyGivenUp;
    }
}
