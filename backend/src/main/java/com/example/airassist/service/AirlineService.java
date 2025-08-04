package com.example.airassist.service;

import com.example.airassist.persistence.dao.AirlineRepository;
import com.example.airassist.persistence.model.Airline;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@Data
@AllArgsConstructor
public class AirlineService {
    private final AirlineRepository airlineRepository;

    public List<Airline> getAllAirlines() {
        log.info("Fetching all airlines from the database");
        List<Airline> airlines = airlineRepository.findAll();
        log.info("Fetched {} airlines from the database", airlines.size());
        return airlines;
    }
}
