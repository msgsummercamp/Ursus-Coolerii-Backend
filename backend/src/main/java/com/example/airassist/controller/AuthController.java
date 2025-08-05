package com.example.airassist.controller;

import com.example.airassist.service.AuthService;
import com.example.airassist.common.dto.LoginRequest;
import com.example.airassist.common.dto.LoginResponse;
import com.example.airassist.common.dto.SignupRequest;
import com.example.airassist.common.dto.SignupResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@Validated
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService AuthService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest){
        log.info("Login request received : {}", loginRequest);

        LoginResponse loginResponse = AuthService.login(loginRequest);
        log.info("Logged in user: {}", loginResponse);

        return ResponseEntity.status(HttpStatus.CREATED).body(loginResponse);
    }

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@RequestBody SignupRequest signupRequest){
        log.info("Signup request received : {}", signupRequest);

        AuthService.signup(signupRequest);
        log.info("Signed up  user: {}", signupRequest);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
