package com.example.airassist.common.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaveRequest {
    private CaseRequest caseRequest;
    private SignupRequest signupRequest;
}
