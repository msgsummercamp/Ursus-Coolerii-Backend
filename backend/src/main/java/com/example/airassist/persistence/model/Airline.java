package com.example.airassist.persistence.model;

import com.opencsv.bean.CsvBindByName;
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
    @CsvBindByName(column = "Name")
    private String name;
    @CsvBindByName(column = "IATA")
    private String iataCode;
}