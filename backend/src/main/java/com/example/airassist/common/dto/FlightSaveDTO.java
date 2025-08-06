package com.example.airassist.common.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlightSaveDTO {
    private String flightNumber;
    private String airlineName;
    private String departureAirport;
    private String destinationAirport;
    private boolean isFirstFlight;
    private boolean isLastFlight;
    private boolean problemFlight;
    private Timestamp departureTime;
    private Timestamp arrivalTime;
}
