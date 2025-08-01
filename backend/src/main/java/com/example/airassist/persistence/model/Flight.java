package com.example.airassist.persistence.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import java.sql.Timestamp;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "flights")
public class Flight {
    @Id
    @NotBlank(message = "Flight should have a flight number")
    private String flightId;

    @NotBlank(message = "Flight should have a departure date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd")
    private Date flightDate;

    @ManyToOne
    @JoinColumn(name = "airline_id")
    private Airline airline;

    @NotBlank(message = "Flight should have a departure airport")
    private String departureAirport;

    @NotBlank(message = "Flight should have an arrival airport")
    private String destinationAirport;

    @NotBlank(message = "Flight should have a departure time")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Timestamp departureTime;

    @NotBlank(message = "Flight should have an arrival time")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Timestamp arrivalTime;
}
