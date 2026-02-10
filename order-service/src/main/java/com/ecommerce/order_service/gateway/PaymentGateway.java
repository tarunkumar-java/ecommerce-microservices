package com.ecommerce.order_service.gateway;

import com.ecommerce.order_service.client.PaymentClient;
import com.ecommerce.order_service.dto.PaymentRequestDto;
import com.ecommerce.order_service.dto.PaymentResponseDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import org.example.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class PaymentGateway {

    private final PaymentClient paymentClient;

    @CircuitBreaker(name = "paymentService")
    @Retry(name = "paymentService")
    public ApiResponse<PaymentResponseDto> pay(PaymentRequestDto req) {
        return paymentClient.makePayment(req).getBody();
    }
}


