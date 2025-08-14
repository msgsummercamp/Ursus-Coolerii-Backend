package com.example.airassist.controller;

import com.example.airassist.common.dto.*;
import com.example.airassist.persistence.model.CaseFile;
import com.example.airassist.persistence.model.Passenger;
import com.example.airassist.service.AuthService;
import com.example.airassist.service.CaseFileService;
import com.example.airassist.util.PdfGenerator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
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
    public ResponseEntity<UUID> saveCase(@RequestPart("saveRequest") SaveRequest saveRequest,
                                         @RequestPart("files") MultipartFile[] uploadedDocuments) {
        log.info("Save case request received: {}", saveRequest);
        SignupRequest  signupRequest = saveRequest.getSignupRequest();

        if(signupRequest != null)
            authService.signup(signupRequest);
        CaseFile savedCaseFile =  caseFileService.saveCase(saveRequest.getCaseRequest(), Arrays.asList(uploadedDocuments));

        log.info("Save case successfully: {}", savedCaseFile);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCaseFile.getCaseId());
    }

    @GetMapping
    public ResponseEntity<Page<CaseFileSummaryDTO>> getAllCases(
            @RequestParam(defaultValue = "0") Integer pageIndex,
            @RequestParam(defaultValue = "5") Integer pageSize,
            @RequestParam(value = "passengerId", required = false) UUID passengerId
    ) {
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        Page<CaseFileSummaryDTO> cases;
        if (passengerId != null) {
            cases = caseFileService.getCaseSummariesByPassengerId(passengerId, pageable);
        } else {
            cases = caseFileService.findAll(pageable);
        }
        if (cases.isEmpty()) {
            log.warn("No cases found");
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(cases);
    }
  
    @GetMapping("/contract/{caseId}")
    public ResponseEntity<CaseDetailsDTO> getCaseDetailsByCaseId(@PathVariable UUID caseId) {
        log.info("Get case details by case ID request received: {}", caseId);
        CaseDetailsDTO details = caseFileService.getCaseDetailsByCaseId(caseId);
        log.info("Case details response: {}", details);
        return ResponseEntity.ok(caseFileService.getCaseDetailsByCaseId(caseId));
    }

    @GetMapping("/pdf/{caseId}")
    public ResponseEntity<byte[]> downloadCasePdf(@PathVariable("caseId") UUID caseId) {
        CaseFile caseFile = caseFileService.findCaseFileById(caseId);
        Passenger passenger = caseFile.getPassenger();

        byte[] pdfBytes;
        try {
            LocalDate caseDateOnly = caseFile.getCaseDate().toLocalDateTime().toLocalDate();
            String formattedDate = caseDateOnly.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            pdfBytes = PdfGenerator.generateCasePdf(
                    caseFile.getContractId(),
                    formattedDate,
                    passenger.getFirstName(),
                    passenger.getLastName(),
                    caseFile.getReservationNumber()
            );
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "attachment; filename=case_" + caseId + ".pdf")
                .body(pdfBytes);
    }
}
