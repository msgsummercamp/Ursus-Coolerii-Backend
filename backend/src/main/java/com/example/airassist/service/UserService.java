package com.example.airassist.service;


import com.example.airassist.persistence.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    Optional<User> findByEmail(String email);
    Optional<User> save(User user);
    void deleteById(UUID id);
    Page<User> findAll(Pageable pageable);
    Optional<User> findById(UUID id);
    Optional<User> update(User user);
    Optional<User> patch(User user);
}
