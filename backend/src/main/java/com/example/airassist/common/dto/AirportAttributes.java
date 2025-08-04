package com.example.airassist.common.dto;

import lombok.Data;

@Data
public class AirportAttributes {
    private String name;
    private String city;
    private String country;
    private String iata;
    private String icao;
    private String latitude;
    private String longitude;
    private Long altitude;
    private String timezone;

}
