package com.example.airassist.persistence.dao;

import com.example.airassist.persistence.model.CaseFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CaseFileRepository extends JpaRepository<CaseFile, UUID> {
    @Query(value = "SELECT COUNT(*) FROM cases WHERE user_id = :userId", nativeQuery = true)
    int countByUserId(@Param("userId") UUID userId);
    @Query(value = "SELECT * FROM cases WHERE user_id = :userId", nativeQuery = true)
    Page<CaseFile> findByUserId(@Param("userId") UUID userId, Pageable pageable);
}
