package com.example.airassist.common.dto;

import com.example.airassist.persistence.model.Role;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
public class AuthenticatedUserDTO {
    private String firstName;
    private String lastName;
    private Set<Role> roles;
}
