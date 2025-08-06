package com.example.airassist.persistence.dao;

import com.example.airassist.persistence.model.Airline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AirlineRepository extends JpaRepository<Airline, UUID> {
    Optional<Airline> findFirstByName(String name);
}