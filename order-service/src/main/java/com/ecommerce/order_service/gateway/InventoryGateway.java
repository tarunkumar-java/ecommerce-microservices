package com.ecommerce.order_service.gateway;

import com.ecommerce.order_service.client.InventoryClient;
import com.ecommerce.order_service.dto.InventoryReserveRequest;
import com.ecommerce.order_service.dto.InventoryResponseDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.example.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@RequiredArgsConstructor
public class InventoryGateway {

    private final InventoryClient inventoryClient;

    // ---------- GET INVENTORY ----------
    @CircuitBreaker(name = "inventoryService", fallbackMethod = "getInventoryFallback")
    @Retry(name = "inventoryService")
    public ApiResponse<InventoryResponseDTO> getInventory(Long productId) {
        return inventoryClient.getInventoryById(productId);
    }

    public ApiResponse<InventoryResponseDTO> getInventoryFallback(Long productId, Throwable t) {
        return ApiResponse.failure("Inventory service unavailable");
    }

    // ---------- RESERVE STOCK ----------
    @CircuitBreaker(name = "inventoryService", fallbackMethod = "reserveFallback")
    @Retry(name = "inventoryService")
    public ResponseEntity<String> reserveStock(InventoryReserveRequest request) {
        return inventoryClient.reserveStock(request);
    }

    public ResponseEntity<String> reserveFallback(InventoryReserveRequest request, Throwable t) {
        return ResponseEntity
                .status(503)
                .body("Inventory service unavailable (reserve)");
    }

    // ---------- RELEASE STOCK ----------
    @CircuitBreaker(name = "inventoryService", fallbackMethod = "releaseFallback")
    @Retry(name = "inventoryService")
    public ResponseEntity<String> releaseStock(InventoryReserveRequest request) {
        return inventoryClient.releaseStock(request);
    }

    public ResponseEntity<String> releaseFallback(InventoryReserveRequest request, Throwable t) {
        return ResponseEntity
                .status(503)
                .body("Inventory service unavailable (release)");
    }

    // ---------- CONFIRM STOCK ----------
    @CircuitBreaker(name = "inventoryService", fallbackMethod = "confirmFallback")
    @Retry(name = "inventoryService")
    public ResponseEntity<String> confirmStock(InventoryReserveRequest request) {
        return inventoryClient.confirmStock(request);
    }

    public ResponseEntity<String> confirmFallback(InventoryReserveRequest request, Throwable t) {
        return ResponseEntity
                .status(503)
                .body("Inventory service unavailable (confirm)");
    }
}

