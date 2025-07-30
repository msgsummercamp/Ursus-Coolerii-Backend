package com.example.airassist.airport.service;

import com.example.airassist.persistence.dao.AirportRepository;
import com.example.airassist.dto.AirportAttributes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Slf4j
public class AirportService {
    private final AirportRepository airportRepository;

    @Autowired
    public AirportService(AirportRepository airportRepository){
        this.airportRepository = airportRepository;
    }

    public List<AirportAttributes> getAllAirports(){
        log.info("Finding all airports....");
        List<AirportAttributes> airports = airportRepository.findAll();
        log.info("Found {} airports", airports.size());
        return airports;
    }

}
