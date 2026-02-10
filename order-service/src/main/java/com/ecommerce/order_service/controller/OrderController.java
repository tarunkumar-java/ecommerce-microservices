package com.ecommerce.order_service.controller;

import com.ecommerce.order_service.dto.OrderRequestDTO;
import com.ecommerce.order_service.dto.OrderResponseDTO;
import com.ecommerce.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.example.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // Create new order
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponseDTO>> createOrder(
            @RequestBody OrderRequestDTO request) throws ExecutionException, InterruptedException {

        ApiResponse<OrderResponseDTO> response = orderService.createOrder(request);
        return ResponseEntity.ok(response);
    }

    /*// Get order by orderId
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDTO> getOrder(
            @PathVariable String orderId) {

        OrderResponseDTO response = orderService.getOrder(orderId);
        return ResponseEntity.ok(response);
    }*/
}
