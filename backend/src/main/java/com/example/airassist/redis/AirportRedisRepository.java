package com.example.airassist.redis;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AirportRedisRepository extends CrudRepository<Airport, String> {
}
