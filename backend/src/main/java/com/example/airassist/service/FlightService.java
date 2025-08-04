package com.example.airassist.service;

import com.example.airassist.persistence.model.Flight;

import java.util.Optional;

public interface FlightService {
    Optional<Flight> save(Flight flight);
}
