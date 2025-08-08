package com.example.airassist.controller;

import com.example.airassist.service.EmailCheckService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class EmailController {

    private EmailCheckService emailCheckService;

    @GetMapping("/email-exists")
    public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestParam String email) {
        boolean exists = emailCheckService.checkEmailExists(email);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

}
