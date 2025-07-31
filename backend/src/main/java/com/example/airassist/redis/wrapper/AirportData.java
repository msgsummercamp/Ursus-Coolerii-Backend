package com.example.airassist.redis.wrapper;

import com.example.airassist.redis.AirportAttributes;
import lombok.Data;

@Data
public class AirportData {
    private String id;
    private String type;
    private AirportAttributes attributes;
}
