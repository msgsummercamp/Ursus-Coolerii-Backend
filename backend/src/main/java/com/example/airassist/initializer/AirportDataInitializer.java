package com.example.airassist.initializer;

import com.example.airassist.persistence.dao.AirportRepository;
import com.example.airassist.redis.Airport;
import com.example.airassist.redis.wrapper.AirportResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.client.HttpClientErrorException.TooManyRequests;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class AirportDataInitializer {
    private final AirportRepository airportRepository;
    private final RestTemplate restTemplate;
    private final RedisTemplate redisTemplate;

    @Value("${api.airport.pageCount}")
    private int pageCount;

    @Value("${api.airport.url}")
    private String url;

    @Autowired
    public AirportDataInitializer(AirportRepository airportRepository,  RestTemplate restTemplate, RedisTemplate redisTemplate) {
        this.airportRepository = airportRepository;
        this.restTemplate = restTemplate;
        this.redisTemplate = redisTemplate;
    }

    @Scheduled(cron = "${api.airport.fetch.time}")
    private void initAirports() throws InterruptedException {
        log.info("Initializing airports....");
        List<Airport> airports = fetchAirportList();
        airportRepository.saveAll(airports);
        redisTemplate.opsForValue().set("airports", airports);
        log.info("Airports initialized");
    }

    private List<Airport> fetchAirportList() throws InterruptedException {
        List<Airport> airports = new ArrayList<>();
        for (int i = 0; i <= pageCount; i++) {
            try {
                airports.addAll(convertAirportFromWrapper(
                        restTemplate.getForObject(i == 0 ? url : url + "?page=" + i, AirportResponse.class)
                ));
                Thread.sleep(100);
            } catch (TooManyRequests e) {
                log.warn("Rate limited, waiting 30 seconds before retrying page {}", i);
                Thread.sleep(30000);
                i--;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Interrupted during sleep: {}", e.getMessage());
            }
        }
        return airports;
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