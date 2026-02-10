package com.ecommerce.order_service.client;

import com.ecommerce.order_service.dto.PaymentRequestDto;
import com.ecommerce.order_service.dto.PaymentResponseDto;
import org.example.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service")
public interface PaymentClient {
    @PostMapping("api/payments")
    public ResponseEntity<ApiResponse<PaymentResponseDto>> makePayment(@RequestBody PaymentRequestDto dto);
}
