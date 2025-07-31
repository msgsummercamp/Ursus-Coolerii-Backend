package com.example.airassist.service;

import com.example.airassist.persistence.model.CaseFile;

import java.util.List;

public interface CaseFileService {
    List<CaseFile> findAllCaseFiles();
    int calculateCaseReward(CaseFile caseFile);
    double calculateDistance(String departureAirport, String destinationAirport);
}
