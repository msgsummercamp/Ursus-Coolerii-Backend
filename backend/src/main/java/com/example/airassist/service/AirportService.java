package com.example.airassist.service;

import com.example.airassist.redis.Airport;
import com.example.airassist.redis.AirportRedisRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Slf4j
@Data
@AllArgsConstructor
public class AirportService {
    private final AirportRedisRepository airportRedisRepository;
    private final RedisTemplate redisTemplate;

    public List<Airport> getAllAirports() {
        List<Airport> cached = (List<Airport>) redisTemplate.opsForValue().get("airports");
        if (cached != null) return cached;
        List<Airport> fromRepo = StreamSupport
                .stream(airportRedisRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
        redisTemplate.opsForValue().set("airports", fromRepo);
        return fromRepo;
    }
}