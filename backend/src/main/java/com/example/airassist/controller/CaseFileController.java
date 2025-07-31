package com.example.airassist.controller;

import com.example.airassist.persistence.model.CaseFile;
import com.example.airassist.service.CaseFileService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/case-files")
@AllArgsConstructor
@RestController
@Slf4j
public class CaseFileController {
    public final CaseFileService caseFileService;

    @GetMapping()
    public Iterable<CaseFile> getAllCaseFiles() {
        return caseFileService.findAllCaseFiles();
    }

    @PostMapping("/calculate-reward")
    public int calculateCaseReward(@RequestBody CaseFile caseFile) {
        return caseFileService.calculateCaseReward(caseFile);
    }
}
