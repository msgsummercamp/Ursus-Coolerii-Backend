package com.example.airassist.airport.controller;

import com.example.airassist.airport.service.AirportService;
import com.example.airassist.dto.AirportAttributes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/airports")
public class AirportController {

    private final AirportService airportService;

    @Autowired
    public AirportController(AirportService airportService) {
        this.airportService = airportService;
    }

    @RequestMapping
    public List<AirportAttributes> getAllAirports(){
        log.info("Get all airports request received");
        List<AirportAttributes> airports = airportService.getAllAirports();
        log.info("Get all airports response, count {}",  airports.size());
        return airports;
    }

}
