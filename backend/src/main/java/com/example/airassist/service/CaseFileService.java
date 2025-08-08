package com.example.airassist.service;

import com.example.airassist.common.dto.CalculateRewardRequest;
import com.example.airassist.common.dto.CaseRequest;
import com.example.airassist.common.dto.CaseFileSummaryDTO;
import com.example.airassist.common.dto.EligibilityRequest;
import com.example.airassist.common.dto.SaveRequest;
import com.example.airassist.persistence.model.CaseFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CaseFileService {
    Boolean isEligible(EligibilityRequest eligibilityRequest);
    List<CaseFile> findAllCaseFiles();
    int calculateCaseReward(CalculateRewardRequest calculateRewardRequest);
    CaseFile saveCase(CaseRequest saveRequest, List<MultipartFile> uploadedDocuments);
    List<CaseFileSummaryDTO> getAllCaseSummaries();
}
