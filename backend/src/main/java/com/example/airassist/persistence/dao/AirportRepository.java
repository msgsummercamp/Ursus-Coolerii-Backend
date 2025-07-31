package com.example.airassist.persistence.dao;

import com.example.airassist.redis.Airport;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AirportRepository extends CrudRepository<Airport, String> {

}
