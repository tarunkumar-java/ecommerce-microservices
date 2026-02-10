package com.ecommerce.order_service.client;

import com.ecommerce.order_service.dto.UserResponseDTO;
import org.example.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserClient {
    @GetMapping("api/users/{userId}")
    ApiResponse<UserResponseDTO> getUserById(@PathVariable int userId);
}

