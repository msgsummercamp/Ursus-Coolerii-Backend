package com.example.airassist.service;

import com.example.airassist.persistence.model.Airline;

import java.util.List;

public interface AirlineService {

    public List<Airline> getAllAirlines();
    public Airline getAirlineByName(String name);
}
