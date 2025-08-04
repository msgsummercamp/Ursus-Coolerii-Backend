package com.example.airassist.common.dto.wrapper;

import com.example.airassist.common.dto.AirportAttributes;
import lombok.Data;

@Data
public class AirportData {
    private String id;
    private String type;
    private AirportAttributes attributes;
}
