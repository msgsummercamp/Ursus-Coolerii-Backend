package com.example.airassist.controller;

import com.example.airassist.service.AuthService;
import com.example.airassist.common.dto.LoginRequest;
import com.example.airassist.common.dto.LoginResponse;
import com.example.airassist.common.dto.SignupRequest;
import com.example.airassist.common.dto.SignupResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
        String cookieValue = String.format(
                "jwt=%s; Max-Age=%d; Path=/; HttpOnly; SameSite=None; Secure=false",
                token, 60 * 60
        );
        response.addHeader("Set-Cookie", cookieValue);
    }



    @GetMapping("/check")
    public ResponseEntity<Map<String, Boolean>> checkLogin(HttpServletRequest request){
        log.info("Check login request received : {}", request.getRequestURI());
        String token = getJwtFromCookies(request);
        authService.checkLogged(token);
        return ResponseEntity.ok(Map.of("loggedIn", true));

    }

    private String getJwtFromCookies(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
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
