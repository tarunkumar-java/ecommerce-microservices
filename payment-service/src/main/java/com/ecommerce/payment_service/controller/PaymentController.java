package com.ecommerce.payment_service.controller;

import com.ecommerce.payment_service.dto.*;
import com.ecommerce.payment_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.example.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /*@PostMapping
    public ResponseEntity<ApiResponse<PaymentResponseDto>> makePayment(@RequestBody PaymentRequestDto dto) {
        return ResponseEntity.ok(paymentService.processPayment(dto));
    }*/
}
