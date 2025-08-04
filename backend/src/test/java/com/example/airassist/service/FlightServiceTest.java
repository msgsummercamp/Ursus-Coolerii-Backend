package com.example.airassist.service;

import com.example.airassist.persistence.dao.FlightRepository;
import com.example.airassist.persistence.model.Airline;
import com.example.airassist.persistence.model.Flight;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("FlightService Tests")
class FlightServiceTest {

    @Mock
    private FlightRepository flightRepository;

    private FlightService flightService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        flightService = new FlightServiceImpl(flightRepository);
    }

    @Test
    @DisplayName("Should generate correct flightId and save flight")
    void testSaveFlight_GeneratesFlightId() {
        Airline airline = new Airline(UUID.randomUUID(), "Lufthansa", "LH");
        LocalDateTime departureTime = LocalDateTime.of(2025, 9, 12, 10, 30);
        Flight flight = new Flight(
                null,
                "LH123",
                airline,
                "FRA",
                "JFK",
                Timestamp.valueOf(departureTime),
                Timestamp.valueOf(departureTime.plusHours(8))
        );
        when(flightRepository.save(any(Flight.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Optional<Flight> savedFlight = flightService.save(flight);
        assertTrue(savedFlight.isPresent());
        assertEquals("LH123-2025-09-12-FRA", savedFlight.get().getFlightId());
        verify(flightRepository, times(1)).save(any(Flight.class));
    }

    @Test
    @DisplayName("Should save and return flight with formatted ID (no spaces)")
    void testSaveFlight_StripsWhitespace() {
        Flight flight = new Flight();
        flight.setFlightNumber(" KL 456 ");
        flight.setDepartureAirport(" AMS ");
        flight.setDepartureTime(Timestamp.valueOf("2025-10-01 15:00:00"));
        flight.setArrivalTime(Timestamp.valueOf("2025-10-01 18:00:00"));
        flight.setAirline(new Airline(UUID.randomUUID(), "KLM", "KL"));
        when(flightRepository.save(any(Flight.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Optional<Flight> savedFlight = flightService.save(flight);
        assertTrue(savedFlight.isPresent());
        assertEquals("KL456-2025-10-01-AMS", savedFlight.get().getFlightId());
    }
}