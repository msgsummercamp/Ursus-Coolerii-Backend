package com.example.airassist.service;

import com.example.airassist.persistence.dao.AirlineRepository;
import com.example.airassist.persistence.model.Airline;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AirlineServiceImplTest {
    private AirlineRepository airlineRepository;
    private AirlineServiceImpl airlineService;

    @BeforeEach
    void setUp() {
        airlineRepository = mock(AirlineRepository.class);
        airlineService = new AirlineServiceImpl(airlineRepository);
    }

    @Test
    void testGetAllAirlines_ReturnsAirlineList() {
        Airline airline1 = new Airline(UUID.randomUUID(), "Air France", "AF");
        Airline airline2 = new Airline(UUID.randomUUID(), "Lufthansa", "LH");
        when(airlineRepository.findAll()).thenReturn(Arrays.asList(airline1, airline2));
        List<Airline> result = airlineService.getAllAirlines();
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Air France", result.get(0).getName());
        assertEquals("LH", result.get(1).getIataCode());
        verify(airlineRepository, times(1)).findAll();
    }

    @Test
    void testGetAllAirlines_ReturnsEmptyList() {
        when(airlineRepository.findAll()).thenReturn(List.of());
        List<Airline> result = airlineService.getAllAirlines();
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(airlineRepository, times(1)).findAll();
    }
}
