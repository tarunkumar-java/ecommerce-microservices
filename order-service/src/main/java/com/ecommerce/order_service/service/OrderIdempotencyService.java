package com.ecommerce.order_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class OrderIdempotencyService {

    private final RedisTemplate<String, Object> redisTemplate;

    public boolean isDuplicate(String requestId) {
        return Boolean.TRUE.equals(
                redisTemplate.hasKey("order:req:" + requestId)
        );
    }

    public void markProcessed(String requestId) {
        redisTemplate.opsForValue()
                .set("order:req:" + requestId, "DONE", Duration.ofMinutes(10));
    }
}
