package com.example.airassist.controller;

import com.example.airassist.dto.EligibilityRequest;
import com.example.airassist.service.CaseFileService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/case-file")
public class CaseFileController {
    private CaseFileService caseFileService;

    @PostMapping("/eligibility")
    public ResponseEntity<Boolean> isEligible(@RequestBody EligibilityRequest eligibilityRequest) {
        return ResponseEntity.ok(caseFileService.isEligible(eligibilityRequest));
    }

}
