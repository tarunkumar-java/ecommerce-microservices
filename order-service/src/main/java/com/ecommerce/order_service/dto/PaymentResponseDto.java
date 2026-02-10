package com.ecommerce.order_service.dto;

import lombok.Builder;

@Builder
public record PaymentResponseDto(String paymentId,
        String orderId,
        String status) {
}
