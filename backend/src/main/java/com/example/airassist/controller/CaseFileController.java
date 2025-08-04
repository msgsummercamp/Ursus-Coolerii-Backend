package com.example.airassist.controller;

import com.example.airassist.dto.EligibilityRequest;
import com.example.airassist.persistence.model.CaseFile;
import com.example.airassist.service.CaseFileService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/case-files")
@AllArgsConstructor
public class CaseFileController {
    public final CaseFileService caseFileService;

    @PostMapping("/eligibility")
    public ResponseEntity<Boolean> isEligible(@RequestBody EligibilityRequest eligibilityRequest) {
        return ResponseEntity.ok(caseFileService.isEligible(eligibilityRequest));
    }

    @GetMapping()
    public Iterable<CaseFile> getAllCaseFiles() {
        return caseFileService.findAllCaseFiles();
    }

    @PostMapping("/calculate-reward")
    public int calculateCaseReward(@RequestBody CaseFile caseFile) {
        return caseFileService.calculateCaseReward(caseFile);
    }
}
