package com.example.airassist.common.dto;

import com.example.airassist.common.enums.Disruption;
import lombok.Data;

@Data
public class EligibilityRequest {
    private Disruption disruption;
    private Integer noticeDays;
    private Boolean arrived;
    private Integer delayHours;
    private Boolean isVoluntarilyGivenUp;
}

