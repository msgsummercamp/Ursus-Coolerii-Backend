package com.example.airassist.service;

import com.example.airassist.dto.LoginRequest;
import com.example.airassist.dto.LoginResponse;
import com.example.airassist.dto.SignupRequest;
import com.example.airassist.dto.SignupResponse;

public interface AuthService {
    LoginResponse login(LoginRequest loginRequest);
    SignupResponse signup(SignupRequest signupRequest);
}
