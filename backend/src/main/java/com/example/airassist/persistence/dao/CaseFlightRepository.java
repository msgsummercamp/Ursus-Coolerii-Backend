package com.example.airassist.persistence.dao;

import com.example.airassist.persistence.model.CaseFlights;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CaseFlightRepository extends JpaRepository<CaseFlights, Long> {
}
