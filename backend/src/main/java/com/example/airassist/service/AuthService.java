package com.example.airassist.service;


import com.example.airassist.common.dto.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.UUID;

public interface AuthService {
    LoginResponse login(LoginRequest loginRequest);
    void signup(SignupRequest signupRequest);
    boolean checkLogged(String token);
    boolean checkMatchID(String token, UUID passengerId);
    boolean checkMatchID(String token, UUID passengerId);
}
