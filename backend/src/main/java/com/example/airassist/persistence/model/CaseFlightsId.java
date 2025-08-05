package com.example.airassist.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CaseFlightsId implements Serializable {
    @Column(name = "case_id")
    private UUID caseId;

    @Column(name = "flight_id")
    private String flightId;

}