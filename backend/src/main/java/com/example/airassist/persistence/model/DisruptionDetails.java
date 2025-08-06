package com.example.airassist.persistence.model;

import com.example.airassist.common.enums.Disruption;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DisruptionDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Disruption disruption;
    private Integer noticeDays;
    private Boolean arrived;
    private Integer delayHours;
    private Boolean isVoluntarilyGivenUp;

}
