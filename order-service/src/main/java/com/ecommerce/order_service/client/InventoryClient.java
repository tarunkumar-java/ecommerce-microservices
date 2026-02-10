package com.ecommerce.order_service.client;

import com.ecommerce.order_service.dto.InventoryReserveRequest;
import com.ecommerce.order_service.dto.InventoryResponseDTO;
import org.example.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "inventory-service")
public interface InventoryClient {
    @GetMapping("api/inventory/{productId}")
    public ApiResponse<InventoryResponseDTO> getInventoryById(@PathVariable long productId);

    // Reserve stock
    @PostMapping("api/inventory/reserve")
    public ResponseEntity<String> reserveStock(
            @RequestBody InventoryReserveRequest request);

    // Release stock
    @PostMapping("api/inventory/release")
    public ResponseEntity<String> releaseStock(
            @RequestBody InventoryReserveRequest request);

    //Confirm Stock
    @PostMapping("api/inventory/confirmStock")
    public ResponseEntity<String> confirmStock(@RequestBody InventoryReserveRequest request);
}

