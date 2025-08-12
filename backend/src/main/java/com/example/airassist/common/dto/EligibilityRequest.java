package com.example.airassist.common.dto;

import com.example.airassist.common.enums.DaysBeforeNotice;
import com.example.airassist.common.enums.Disruption;
import com.example.airassist.common.enums.HoursBeforeArrival;
import lombok.Data;

@Data
public class EligibilityRequest {
    private Disruption disruption;
    private DaysBeforeNotice noticeDays;
    private Boolean arrived;
    private HoursBeforeArrival delayHours;
    private Boolean isVoluntarilyGivenUp;
}

