package com.example.airassist.dto.wrapper;

import lombok.Data;
import java.util.List;

@Data
public class AirportResponse {
    List<AirportData> data;
}
