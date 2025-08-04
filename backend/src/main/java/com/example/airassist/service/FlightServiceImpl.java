package com.example.airassist.service;

import com.example.airassist.persistence.dao.FlightRepository;
import com.example.airassist.persistence.model.Flight;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class FlightServiceImpl implements FlightService {
    private final FlightRepository flightRepository;

    @Override
    public Optional<Flight> save(Flight flight) {
        log.info("Saving flight: {}", flight);
        String flightId = String.format(
                "%s-%s-%s",
                flight.getFlightNumber(),
                flight.getDepartureTime().toLocalDateTime().toLocalDate(),
                flight.getDepartureAirport()
        ).toUpperCase().replaceAll("\\s+", "");
        flight.setFlightId(flightId);
        flightRepository.save(flight);
        return Optional.of(flight);
    }
}