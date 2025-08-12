package com.example.airassist.common.dto;

import com.example.airassist.persistence.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private Set<Role> role;
    private int casesCount;
}
