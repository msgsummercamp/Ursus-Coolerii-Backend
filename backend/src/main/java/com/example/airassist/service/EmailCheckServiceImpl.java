package com.example.airassist.service;

import com.example.airassist.persistence.dao.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;

@Service
@Slf4j
public class EmailCheckServiceImpl implements  EmailCheckService {

    private RedisTemplate redisTemplate;
    private UserRepository userRepository;

    public EmailCheckServiceImpl(RedisTemplate redisTemplate, UserRepository userRepository) {
        this.redisTemplate = redisTemplate;
        this.userRepository = userRepository;
    }

    @Value("${redis.email.existence.key}")
    private String REDIS_PREFIX;

    public boolean checkEmailExists(String email) {
        String key = REDIS_PREFIX + email;

        log.info("Checking email exists for key {} in cached", key);
        Boolean cached = (Boolean) redisTemplate.opsForValue().get(key);
        if (cached != null) {
            log.info("Email exists in cached for key {}", key);
            return cached;
        }
        log.info("Email does not exist in cached for key {}", key);

        boolean exists = userRepository.existsByEmail(email);
        redisTemplate.opsForValue().set(key, exists, Duration.ofHours(1));
        return exists;
    }
}
