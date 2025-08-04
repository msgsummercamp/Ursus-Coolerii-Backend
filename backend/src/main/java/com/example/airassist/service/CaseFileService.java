package com.example.airassist.service;

import com.example.airassist.common.dto.EligibilityRequest;
import com.example.airassist.persistence.model.CaseFile;

import java.util.List;

public interface CaseFileService {
    Boolean isEligible(EligibilityRequest eligibilityRequest);
    List<CaseFile> findAllCaseFiles();
    int calculateCaseReward(CaseFile caseFile);
    double calculateDistance(String departureAirport, String destinationAirport);
}
