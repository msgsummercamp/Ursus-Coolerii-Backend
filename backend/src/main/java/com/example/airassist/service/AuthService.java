package com.example.airassist.service;


import com.example.airassist.common.dto.*;

public interface AuthService {
    LoginResponse login(LoginRequest loginRequest);
    void signup(SignupRequest signupRequest);
    boolean checkLogged(String token);
    AuthenticatedUserDTO getAuthenticatedUserDTO(String token);
}
