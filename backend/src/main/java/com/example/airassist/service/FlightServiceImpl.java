package com.example.airassist.service;

import com.example.airassist.persistence.dao.FlightRepository;
import com.example.airassist.persistence.model.Flight;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
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

    @Override
    public List<Flight> saveAll(List<Flight> flights) {
        log.info("Saving flights: {}", flights.size());
        return flights.stream().map(f -> save(f).orElseThrow(() ->{
            log.error("Error saving flight: {}", f);
            return new RuntimeException("Error saving flight " + f);
        })).toList();
    }
}