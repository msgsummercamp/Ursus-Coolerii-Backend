package com.example.airassist.redis.wrapper;

import lombok.Data;
import java.util.List;

@Data
public class AirportResponse {
    List<AirportData> data;
}
