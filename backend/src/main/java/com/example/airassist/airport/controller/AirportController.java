package com.example.airassist.airport.controller;

import com.example.airassist.airport.service.AirportService;
import com.example.airassist.dto.AirportAttributes;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/airports")
@AllArgsConstructor
public class AirportController {
    private final AirportService airportService;

    @RequestMapping
    public List<AirportAttributes> getAllAirports(){
        log.info("Get all airports request received");
        List<AirportAttributes> airports = airportService.getAllAirports();
        log.info("Get all airports response, count {}",  airports.size());
        return airports;
    }

}
