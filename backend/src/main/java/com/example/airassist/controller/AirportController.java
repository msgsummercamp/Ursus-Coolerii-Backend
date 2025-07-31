package com.example.airassist.controller;

import com.example.airassist.redis.Airport;
import com.example.airassist.service.AirportService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@Slf4j
@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/api/airports")
public class AirportController {
    private final AirportService airportService;

    @RequestMapping
    public List<Airport> getAllAirports(){
        log.info("Get all airports request received");
        List<Airport> airports = airportService.getAllAirports();
        log.info("Get all airports response, count {}",  airports.size());
        return airports;
    }
}
