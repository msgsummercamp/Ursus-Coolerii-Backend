package com.example.airassist.persistence.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "case_flights")
public class CaseFlights {
    @EmbeddedId
    private CaseFlightsId id;

    @JsonBackReference
    @ManyToOne
    @MapsId("caseId")
    @JoinColumn(name = "case_id")
    private CaseFile caseFile;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @MapsId("flightId")
    @JoinColumn(name = "flight_id")
    private Flight flight;

    private boolean isProblemFlight;
    private boolean isFirst;
    private boolean isLast;
}

