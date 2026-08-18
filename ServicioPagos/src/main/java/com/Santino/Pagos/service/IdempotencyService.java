package com.Santino.Pagos.service;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class IdempotencyService {

    private static final String PREFIX = "idempotency:";
    private static final Duration TTL = Duration.ofHours(24);

    private final StringRedisTemplate redis;

    public IdempotencyService(StringRedisTemplate redis) {
        this.redis = redis;
    }

    public boolean esPrimeraVez(String idempotencyKey) {
        Boolean primera = redis.opsForValue()
                .setIfAbsent(PREFIX + idempotencyKey, "processed", TTL);
        return Boolean.TRUE.equals(primera);
    }

    public void liberar(String idempotencyKey) {
        redis.delete(PREFIX + idempotencyKey);
    }
}
