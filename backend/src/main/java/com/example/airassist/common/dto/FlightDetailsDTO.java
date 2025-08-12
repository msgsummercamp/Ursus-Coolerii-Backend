package com.example.airassist.common.dto;

import lombok.Data;
import java.sql.Timestamp;

@Data
public class FlightDetailsDTO {
    private String flightNumber;
    private String airline;
    private String reservationNumber;
    private String departureAirport;
    private String destinationAirport;
    private boolean problemFlight;
    private Timestamp plannedDepartureTime;
    private Timestamp plannedArrivalTime;
}