package com.example.airassist.service;

import com.example.airassist.dto.EligibilityRequest;
import org.springframework.stereotype.Service;

@Service
public interface CaseFileService {
    Boolean isEligible(EligibilityRequest eligibilityRequest);
}