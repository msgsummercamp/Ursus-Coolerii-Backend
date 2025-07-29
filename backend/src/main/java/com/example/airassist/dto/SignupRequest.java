package com.example.airassist.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class SignupRequest {

    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
}
