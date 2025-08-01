package com.example.airassist.persistence.dao;

import com.example.airassist.persistence.model.CaseFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CaseFileRepository extends JpaRepository<CaseFile, Long> {
}
