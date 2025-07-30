package com.example.airassist.dto.wrapper;

import com.example.airassist.dto.AirportAttributes;
import lombok.Data;

@Data
public class AirportData {
    private String id;
    private String type;
    private AirportAttributes attributes;
}
