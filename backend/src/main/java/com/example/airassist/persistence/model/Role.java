package com.example.airassist.persistence.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "roles")
@AllArgsConstructor
public class Role {
    @GeneratedValue(strategy = GenerationType.UUID)
    private @Id UUID id;
    private String name;
}