package com.example.airassist.controller;

import com.example.airassist.common.dto.*;
import com.example.airassist.persistence.model.CaseFile;
import com.example.airassist.persistence.model.Document;
import com.example.airassist.service.AuthService;
import com.example.airassist.service.CaseFileService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/case-files")
@AllArgsConstructor
@Slf4j
@Validated
public class CaseFileController {
    private final CaseFileService caseFileService;
    private final AuthService authService;

    @PostMapping("/eligibility")
    public ResponseEntity<Boolean> isEligible(@RequestBody EligibilityRequest eligibilityRequest) {
        return ResponseEntity.ok(caseFileService.isEligible(eligibilityRequest));
    }


    @PostMapping("/calculate-reward")
    public int calculateCaseReward(@RequestBody CalculateRewardRequest calculateRewardRequest) {
        return caseFileService.calculateCaseReward(calculateRewardRequest);
    }


    @PostMapping
    public ResponseEntity<CaseFile> saveCase(@RequestPart("saveRequest") SaveRequest saveRequest,
                                             @RequestPart("files") MultipartFile[] uploadedDocuments) {
        log.info("Save case request received: {}", saveRequest);
        SignupRequest  signupRequest = saveRequest.getSignupRequest();

        if(signupRequest != null)
            authService.signup(signupRequest);
        CaseFile savedCaseFile =  caseFileService.saveCase(saveRequest.getCaseRequest(), Arrays.asList(uploadedDocuments));

        log.info("Save case successfully: {}", savedCaseFile);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<CaseFileSummaryDTO>> getAllCases() {
        log.info("Get all cases request received");
        List<CaseFileSummaryDTO> cases = caseFileService.getAllCaseSummaries();
        log.info("Get all cases response, count {}", cases.size());
        return ResponseEntity.ok(cases);
    }
  
    @GetMapping("/contract/{caseId}")
    public ResponseEntity<CaseDetailsDTO> getCaseDetailsByCaseId(@PathVariable UUID caseId) {
        log.info("Get case details by case ID request received: {}", caseId);
        CaseDetailsDTO details = caseFileService.getCaseDetailsByCaseId(caseId);
        log.info("Case details response: {}", details);
        return ResponseEntity.ok(caseFileService.getCaseDetailsByCaseId(caseId));
    }
  
    @GetMapping("/passenger")
    public ResponseEntity<List<CaseFileSummaryDTO>> getCasesForPassenger(@RequestParam("passengerId") Long passengerId) {
        List<CaseFileSummaryDTO> cases = caseFileService.getCaseSummariesByPassengerId(passengerId);
        return ResponseEntity.ok(cases);
    }
}
