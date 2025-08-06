package com.example.airassist.redis;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RedisHash("Airport")
public class Airport implements Serializable {
    @Id
    private String id;
    private String name;
    private String iata;
}
