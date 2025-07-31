package com.example.airassist.service;

import com.example.airassist.dto.EligibilityRequest;
import com.example.airassist.persistence.dao.CaseFileRepository;
import com.example.airassist.persistence.model.CaseFile;
import com.example.airassist.persistence.model.CaseFlights;
import com.example.airassist.persistence.model.Flight;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

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

    @Override
    public List<CaseFile> findAllCaseFiles() {
        return caseFileRepository.findAll();
    }

    @Override
    public int calculateCaseReward(CaseFile caseFile) {
        if (caseFile == null || caseFile.getCaseFlights() == null || caseFile.getCaseFlights().isEmpty()) {
            log.warn("CaseFile or its flights are null or empty");
            return 0;
        }

        CaseFlights firstFlight = caseFile.getCaseFlights().stream()
                .filter(CaseFlights::isFirst)
                .findFirst()
                .orElse(null);

        CaseFlights lastFlight = caseFile.getCaseFlights().stream()
                .filter(CaseFlights::isLast)
                .findFirst()
                .orElse(null);

        if (firstFlight == null || lastFlight == null) {
            log.warn("First or last flight not found in CaseFile");
            return 0;
        }

        Flight departure = firstFlight.getFlight();
        Flight arrival = lastFlight.getFlight();
        double distance = calculateDistance(departure.getDepartureAirport(), arrival.getDestinationAirport());

        if (distance < LOW_DISTANCE_THRESHOLD)
            return MIN_REWARD;
        else if (distance < HIGH_DISTANCE_THRESHOLD)
            return MED_REWARD;
        else return MAX_REWARD;
    }

    @Override
    public double calculateDistance(String departureAirport, String destinationAirport) {
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
