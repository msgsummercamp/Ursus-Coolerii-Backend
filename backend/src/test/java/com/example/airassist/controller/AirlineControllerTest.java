package com.example.airassist.controller;

import com.example.airassist.persistence.model.Airline;
import com.example.airassist.service.AirlineServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@AutoConfigureMockMvc
public class AirlineControllerTest {
    @Mock
    private AirlineServiceImpl airlineService;

    private AirlineController airlineController;

    @BeforeEach
    void setUp() throws Exception {
        try (var ignored = MockitoAnnotations.openMocks(this)) {
            airlineController = new AirlineController(airlineService);
        }
    }

    @Test
    @DisplayName("Should return a list of airlines")
    void testGetAllAirlines_ReturnsList() {
        Airline airline1 = new Airline(UUID.randomUUID(), "Delta", "DL");
        Airline airline2 = new Airline(UUID.randomUUID(), "United", "UA");
        when(airlineService.getAllAirlines()).thenReturn(Arrays.asList(airline1, airline2));
        List<Airline> result = airlineController.getAllAirlines();
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Delta", result.get(0).getName());
        assertEquals("UA", result.get(1).getIataCode());
    }

    @Test
    @DisplayName("Should return empty list when no airlines are found")
    void testGetAllAirlines_ReturnsEmptyList() {
        when(airlineService.getAllAirlines()).thenReturn(Collections.emptyList());
        List<Airline> result = airlineController.getAllAirlines();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
