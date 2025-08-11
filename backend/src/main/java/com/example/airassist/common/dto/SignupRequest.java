package com.example.airassist.common.dto;

import com.example.airassist.persistence.model.Role;
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
    private String firstName;
    private String lastName;
    private Role role;
}
