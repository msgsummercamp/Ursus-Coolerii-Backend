package com.example.airassist.persistence.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "case_flights")
public class CaseFlights {
    @EmbeddedId
    private CaseFlightsId id;

    @ManyToOne
    @MapsId("caseId")
    @JoinColumn(name = "case_id")
    private Case caseFile;

    @ManyToOne
    @MapsId("flightId")
    @JoinColumn(name = "flight_id")
    private Flight flight;

    private boolean isProblemFlight;
    private boolean isFirst;
    private boolean isLast;
}

