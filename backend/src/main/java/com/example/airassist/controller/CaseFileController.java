package com.example.airassist.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/case-file")
public class CaseFileController {

    @PostMapping("/eligibility/cancellation")
    public boolean isEligibleForCancellation(@RequestParam Integer noticeDays) {
        return noticeDays < 14;
    }

    @PostMapping("/eligibility/delay")
    public boolean isEligibleForDelay(@RequestParam(required = false, defaultValue = "0") Integer delayHours, @RequestParam Boolean arrived) {
        return !arrived || delayHours > 3;
    }

    @PostMapping("/eligibility/denied-boarding")
    public boolean isEligibleForDeniedBoarding(@RequestParam Boolean isVoluntarilyGivenUp) {
        return !isVoluntarilyGivenUp;
    }
}
