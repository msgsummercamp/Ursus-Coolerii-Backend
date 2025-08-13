package com.example.airassist.service;

import com.example.airassist.common.dto.*;
import com.example.airassist.persistence.model.CaseFile;
import com.example.airassist.persistence.model.Document;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface CaseFileService {
    Boolean isEligible(EligibilityRequest eligibilityRequest);
    List<CaseFile> findAllCaseFiles();
    int calculateCaseReward(CalculateRewardRequest calculateRewardRequest);
    CaseFile saveCase(CaseRequest saveRequest, List<MultipartFile> uploadedDocuments);
    List<CaseFileSummaryDTO> getAllCaseSummaries();
    CaseDetailsDTO getCaseDetailsByCaseId(UUID caseId);
    List<CaseFileSummaryDTO> getCaseSummariesByPassengerId(Long passengerId);
    CaseFile findCaseFileById(UUID caseId);
}
