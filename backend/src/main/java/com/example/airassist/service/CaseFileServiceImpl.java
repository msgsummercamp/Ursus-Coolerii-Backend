package com.example.airassist.service;

import com.example.airassist.dto.EligibilityRequest;
import org.springframework.stereotype.Service;

@Service
public class CaseFileServiceImpl implements CaseFileService {
    public Boolean isEligible(EligibilityRequest eligibilityRequest) {
        switch (eligibilityRequest.getDisruption()) {
            case CANCELLATION -> {
                return isEligibleForCancellation(eligibilityRequest.getNoticeDays());
            }
            case DELAY -> {
                return isEligibleForDelay(eligibilityRequest.getDelayHours(), eligibilityRequest.getArrived());
            }
            case DENIED_BOARDING -> {
                return isEligibleForDeniedBoarding(eligibilityRequest.getIsVoluntarilyGivenUp());
            }
            default -> {
                return false;
            }
        }
    }

    private boolean isEligibleForCancellation( Integer noticeDays) {
        return noticeDays < 14;
    }

    private boolean isEligibleForDelay(Integer delayHours, Boolean arrived) {
        return !arrived || delayHours > 3;
    }

    private boolean isEligibleForDeniedBoarding( Boolean isVoluntarilyGivenUp) {
        return !isVoluntarilyGivenUp;
    }
}
