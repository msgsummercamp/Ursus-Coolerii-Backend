package com.example.airassist.persistence.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "airlines")
public class Airline {
    @GeneratedValue(strategy = GenerationType.UUID)
    private @Id UUID airlineId;
    private String name;
    private String iataCode;
}
