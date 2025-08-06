package com.example.airassist.service;

import com.example.airassist.persistence.dao.AirlineRepository;
import com.example.airassist.persistence.model.Airline;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@Data
@AllArgsConstructor
public class AirlineServiceImpl implements AirlineService {
    private final AirlineRepository airlineRepository;

    @Override
    public List<Airline> getAllAirlines() {
        log.info("Fetching all airlines from the database");
        List<Airline> airlines = airlineRepository.findAll();
        log.info("Fetched {} airlines from the database", airlines.size());
        return airlines;
    }

    @Override
    public Airline getAirlineByName(String name) {
        log.info("Fetching airline by name from the database");
        Airline airline = airlineRepository.findFirstByName(name).orElseThrow(
                () -> new EntityNotFoundException("Airline with name " + name + " not found")
        );
        log.info("Airline with name {} has been found", name);
        return airline;
    }

}
