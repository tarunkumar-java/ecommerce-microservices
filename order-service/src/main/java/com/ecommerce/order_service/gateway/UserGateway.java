package com.ecommerce.order_service.gateway;

import com.ecommerce.order_service.client.UserClient;
import com.ecommerce.order_service.dto.UserResponseDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.example.ApiResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserGateway {

    private final UserClient userClient;

    @CircuitBreaker(name = "userService", fallbackMethod = "fallback")
    @Retry(name = "userService")
    public ApiResponse<UserResponseDTO> getUser(Long userId) {
        return userClient.getUserById(Math.toIntExact(userId));
    }

    public ApiResponse<UserResponseDTO> fallback(Long userId, Throwable t) {
        return ApiResponse.failure("User service unavailable");
    }
}