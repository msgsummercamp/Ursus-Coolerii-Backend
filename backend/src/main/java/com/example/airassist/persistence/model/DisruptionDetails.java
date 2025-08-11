package com.example.airassist.persistence.model;

import com.example.airassist.common.enums.DaysBeforeNotice;
import com.example.airassist.common.enums.Disruption;
import com.example.airassist.common.enums.HoursBeforeArrival;
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
    private DaysBeforeNotice noticeDays;
    private Boolean arrived;
    private HoursBeforeArrival delayHours;
    private Boolean isVoluntarilyGivenUp;

}
