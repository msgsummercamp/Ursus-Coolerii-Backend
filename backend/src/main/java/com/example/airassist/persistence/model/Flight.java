package com.example.airassist.persistence.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "flights")
public class Flight {
    private @Id String flightId;

    @NotBlank(message = "Flight should have a flight number")
    private String flightNumber;

    @ManyToOne
    @JoinColumn(name = "airline_id")
    private Airline airline;

    @NotBlank(message = "Flight should have a departure airport")
    private String departureAirport;

    @NotBlank(message = "Flight should have an arrival airport")
    private String destinationAirport;

    @NotNull(message = "Flight should have a departure time")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Timestamp departureTime;

    @NotNull(message = "Flight should have an arrival time")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Timestamp arrivalTime;
}