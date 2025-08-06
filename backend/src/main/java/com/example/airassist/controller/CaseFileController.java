package com.example.airassist.controller;

import com.example.airassist.common.dto.CalculateRewardRequest;
import com.example.airassist.common.dto.EligibilityRequest;
import com.example.airassist.common.dto.SaveCaseRequest;
import com.example.airassist.persistence.model.CaseFile;
import com.example.airassist.service.CaseFileService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

@RestController
@RequestMapping("/api/case-files")
@AllArgsConstructor
@Slf4j
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
    public int calculateCaseReward(@RequestBody CalculateRewardRequest calculateRewardRequest) {
        return caseFileService.calculateCaseReward(calculateRewardRequest);
    }


    @PostMapping
    public ResponseEntity<CaseFile> saveCase(@RequestPart("case") SaveCaseRequest saveCaseRequest,
                                             @RequestPart("files") MultipartFile[] uploadedDocuments) {
        log.info("Save case request received: {}", saveCaseRequest);
        CaseFile savedCaseFile =  caseFileService.saveCase(saveCaseRequest, Arrays.asList(uploadedDocuments));
        log.info("Save case successfully: {}", savedCaseFile);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCaseFile);
    }
}
