package com.example.airassist.controller;

import com.example.airassist.persistence.model.Airline;
import com.example.airassist.service.AirlineServiceImpl;
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
@RequestMapping("/api/airlines")
public class AirlineController {
    private final AirlineServiceImpl airlineService;

    @RequestMapping
    public List<Airline> getAllAirlines() {
        log.info("Get all airlines request received");
        List<Airline> airlines = airlineService.getAllAirlines();
        log.info("Get all airlines response, count {}", airlines.size());
        return airlines;
    }
}
