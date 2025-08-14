package com.example.airassist.controller;

import com.example.airassist.common.dto.*;
import com.example.airassist.service.AuthService;
import com.example.airassist.util.JwtUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;

@Slf4j
@RestController
@Validated
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest,  HttpServletResponse response){
        log.info("Login request received : {}", loginRequest);

        LoginResponse loginResponse = authService.login(loginRequest);
        setCookie(loginResponse.getToken(), response);
        log.info("Logged in user: {}", loginResponse);

        setCookie(loginResponse.getToken(), response);
        return ResponseEntity.status(HttpStatus.CREATED).body(loginResponse);
    }

    private void setCookie(String token, HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("jwt", token)
                .secure(false)
                .path("/")
                .maxAge(Duration.ofHours(1))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

    }



    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@RequestBody SignupRequest signupRequest){
        log.info("Signup request received : {}", signupRequest);

        authService.signup(signupRequest);
        log.info("Signed up  user: {}", signupRequest);

        SignupResponse response = new SignupResponse(null, signupRequest.getFirstName(), signupRequest.getLastName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
