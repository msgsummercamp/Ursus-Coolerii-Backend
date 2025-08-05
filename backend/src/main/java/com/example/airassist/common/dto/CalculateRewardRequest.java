package com.example.airassist.common.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CalculateRewardRequest {
    private String departureAirport;
    private String destinationAirport;

}
