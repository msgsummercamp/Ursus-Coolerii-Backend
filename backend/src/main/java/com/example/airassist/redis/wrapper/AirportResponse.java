package com.example.airassist.redis.wrapper;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class AirportResponse {
    List<AirportData> data;
    private Map<String, String> links;
}