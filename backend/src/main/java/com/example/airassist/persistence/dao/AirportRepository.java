package com.example.airassist.persistence.dao;

import com.example.airassist.dto.AirportAttributes;
import com.example.airassist.dto.wrapper.AirportData;
import com.example.airassist.dto.wrapper.AirportResponse;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class AirportRepository {

    private final RestTemplate restTemplate;

    private List<AirportAttributes> airports = new ArrayList<>();

    @Value("${api.airport.pageCount}")
    private int pageCount;

    @Value("${api.airport.url}")
    private String url;


    public AirportRepository(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private List<AirportAttributes> getAirportsFromResponse(AirportResponse airportResponse) {
        return airportResponse.getData().stream().map(AirportData::getAttributes).collect(Collectors.toList());
    }

    @PostConstruct
    private void initAirports(){
        log.info("Initializing airports....");
        for (int i = 0; i < pageCount; i++){
            AirportResponse airport = restTemplate.getForObject(i == 0 ? url : url + "?page=" + i, AirportResponse.class);
            airports.addAll(getAirportsFromResponse(airport));
        }
        log.info("Airports initialized");
    }

    public List<AirportAttributes> findAll(){
        log.info("Finding all airports....");
        return airports;
    }
}
