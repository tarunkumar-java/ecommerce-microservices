package com.ecommerce.payment_service.client;

import com.ecommerce.payment_service.dto.ProductResponseDTO;
import org.example.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service")
public interface ProductClient {
    @GetMapping("api/products/{productId}")
    public ApiResponse<ProductResponseDTO> getProductById(@PathVariable long productId);
}

