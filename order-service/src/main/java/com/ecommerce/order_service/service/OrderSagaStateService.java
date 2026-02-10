package com.ecommerce.order_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class OrderSagaStateService {

    private final RedisTemplate<String, Object> redisTemplate;

    public void updateState(String orderId, String state) {
        redisTemplate.opsForValue()
                .set("order:saga:" + orderId, state, Duration.ofHours(1));
    }

    public String getState(String orderId) {
        return (String) redisTemplate.opsForValue()
                .get("order:saga:" + orderId);
    }
}
