package com.example.airassist.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class SignupRequest {

    private String email;
    private String password;
    private String firstName;
    private String lastName;
}
