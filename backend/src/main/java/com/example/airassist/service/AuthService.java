package com.example.airassist.service;


import com.example.airassist.common.dto.LoginRequest;
import com.example.airassist.common.dto.LoginResponse;
import com.example.airassist.common.dto.SignupRequest;
import com.example.airassist.common.dto.SignupResponse;

public interface AuthService {
    LoginResponse login(LoginRequest loginRequest);
    void signup(SignupRequest signupRequest);
    boolean checkLogged(String token);
}
