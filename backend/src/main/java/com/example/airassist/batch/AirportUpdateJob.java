package com.example.airassist.batch;

import com.example.airassist.redis.AirportRedisRepository;
import com.example.airassist.redis.Airport;
import com.example.airassist.redis.wrapper.AirportResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.client.HttpClientErrorException.TooManyRequests;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class AirportUpdateJob {
    private final AirportRedisRepository airportRedisRepository;
    private final RestTemplate restTemplate;
    private final RedisTemplate redisTemplate;

    private int pageCount = 0;

    @Value("${api.airport.url}")
    private String url;

    private int extractLastPageIndex(String lastLink) throws URISyntaxException {
        URI uri = new URI(lastLink);
        String query = uri.getQuery();
        if (query != null && query.startsWith("page=")) {
            return Integer.parseInt(query.substring(5));
        }
        return 0;
    }
    private Map<String, String> links;


    @PostConstruct
    private void getLastPageIndexFromApi() {
        try {
            AirportResponse response = restTemplate.getForObject(url, AirportResponse.class);
            String lastLink = response.getLinks().get("last");
            pageCount = extractLastPageIndex(lastLink);
        } catch (Exception e) {
            log.error("Failed to get last page index from API", e);
        }
    }

    @Autowired
    public AirportUpdateJob(AirportRedisRepository airportRedisRepository, RestTemplate restTemplate, RedisTemplate redisTemplate) {
        this.airportRedisRepository = airportRedisRepository;
        this.restTemplate = restTemplate;
        this.redisTemplate = redisTemplate;
    }

    @Scheduled(cron = "${api.airport.fetch.time}")
    private void initAirports() throws InterruptedException {
        log.info("Initializing airports....");
        List<Airport> airports = fetchAirportList();
        airportRedisRepository.saveAll(airports);
        redisTemplate.opsForValue().set("airports", airports);
        log.info("Airports initialized");
    }

    private List<Airport> fetchAirportList() throws InterruptedException {
        List<Airport> airports = new ArrayList<>();
        for (int i = 0; i <= pageCount; i++) {
            try {
                if(i != 0 && i % 100 == 0){
                    Thread.sleep(60000);
                }
                airports.addAll(convertAirportFromWrapper(
                        restTemplate.getForObject(i == 0 ? url : url + "?page=" + i, AirportResponse.class)
                ));
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
                        .iata(data.getAttributes().getIata())
                        .build()).toList();
    }
}