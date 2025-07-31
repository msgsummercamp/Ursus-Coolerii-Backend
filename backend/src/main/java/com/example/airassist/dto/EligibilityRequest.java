package com.example.airassist.dto;

import com.example.airassist.common.Disruption;
import lombok.Data;

@Data
public class EligibilityRequest {
    private Disruption disruption;
    private Integer noticeDays;
    private Boolean arrived;
    private Integer delayHours;
    private Boolean isVoluntarilyGivenUp;
}
