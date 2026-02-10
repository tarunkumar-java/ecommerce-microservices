package com.ecommerce.order_service.gateway;

import com.ecommerce.order_service.client.ProductClient;
import com.ecommerce.order_service.dto.ProductResponseDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.example.ApiResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductGateway {

    private final ProductClient productClient;

    @CircuitBreaker(name = "productService", fallbackMethod = "fallback")
    @Retry(name = "productService")
    public ApiResponse<ProductResponseDTO> getProduct(Long productId) {
        return productClient.getProductById(productId);
    }

    public ApiResponse<ProductResponseDTO> fallback(Long productId, Throwable t) {
        return ApiResponse.failure("Product service is unavailable");
    }
}
