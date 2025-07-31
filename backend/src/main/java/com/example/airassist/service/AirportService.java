package com.example.airassist.service;

import com.example.airassist.redis.Airport;
import com.example.airassist.persistence.dao.AirportRepository;
import com.example.airassist.redis.wrapper.AirportResponse;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Slf4j
@Data
public class AirportService {
    private final AirportRepository airportRepository;
    private final RestTemplate restTemplate;

    @Value("${api.airport.pageCount}")
    private int pageCount;

    @Value("${api.airport.url}")
    private String url;

    @Autowired
    public AirportService(AirportRepository airportRepository, RestTemplate restTemplate) {
        this.airportRepository = airportRepository;
        this.restTemplate = restTemplate;
    }

    public List<Airport> getAllAirports(){
        return StreamSupport.stream(airportRepository.findAll().spliterator(), false).collect(Collectors.toList());
    }

    @Scheduled(fixedDelay = 24, timeUnit = TimeUnit.SECONDS)
    private void initAirports(){
        airportRepository.deleteAll();
        log.info("Initializing airports....");
        List<Airport> airports = new ArrayList<>();
        for (int i = 0; i < pageCount; i++){
            airports.addAll(convertAirportFromWrapper(restTemplate.getForObject(i == 0 ? url : url + "?page=" + i, AirportResponse.class)));
        }
        airportRepository.saveAll(airports);
        log.info("Airports initialized");
    }

    private List<Airport> convertAirportFromWrapper(AirportResponse response){
        return response.getData()
                .stream()
                .map(data ->Airport.builder()
                        .id(data.getId())
                        .name(data.getAttributes().getName())
                        .city(data.getAttributes().getCity())
                        .country(data.getAttributes().getCountry())
                        .icao(data.getAttributes().getIcao())
                        .iata(data.getAttributes().getIata())
                        .latitude(data.getAttributes().getLatitude())
                        .longitude(data.getAttributes().getLongitude())
                        .altitude(data.getAttributes().getAltitude())
                        .timezone(data.getAttributes().getTimezone())
                        .build()).toList();
    }
}