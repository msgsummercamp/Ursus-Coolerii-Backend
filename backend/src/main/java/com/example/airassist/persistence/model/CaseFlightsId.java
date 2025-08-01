package com.example.airassist.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;

@Embeddable
public class CaseFlightsId implements Serializable {
    @Column(name = "case_id")
    private UUID caseId;

    @Column(name = "flight_id")
    private UUID flightId;

}