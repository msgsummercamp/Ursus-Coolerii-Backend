package com.example.airassist.service;

import com.example.airassist.persistence.model.Flight;

import java.util.List;
import java.util.Optional;

public interface FlightService {
    Optional<Flight> save(Flight flight);
    List<Flight> saveAll(List<Flight> flights);
}
