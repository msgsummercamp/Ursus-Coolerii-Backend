package com.example.airassist.service;

import com.example.airassist.common.dto.*;
import com.example.airassist.persistence.model.CaseFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface CaseFileService {
    Boolean isEligible(EligibilityRequest eligibilityRequest);
    int calculateCaseReward(CalculateRewardRequest calculateRewardRequest);
    CaseFile saveCase(CaseRequest saveRequest, List<MultipartFile> uploadedDocuments);
    CaseDetailsDTO getCaseDetailsByCaseId(UUID caseId);
    List<CaseFileSummaryDTO> getCaseSummariesByPassengerId(Long passengerId);
    CaseFile findCaseFileById(UUID caseId);
    Page<CaseFileSummaryDTO> findAll(Pageable pageable);
}
