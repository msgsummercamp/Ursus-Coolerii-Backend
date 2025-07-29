package com.example.airassist.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class CaseFlightsId implements Serializable {
    @Column(name = "case_id")
    private Long caseId;

    @Column(name = "flight_id")
    private Long flightId;

}