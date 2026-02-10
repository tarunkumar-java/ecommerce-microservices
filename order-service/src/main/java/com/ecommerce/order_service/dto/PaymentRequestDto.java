package com.ecommerce.order_service.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PaymentRequestDto(String orderId,
                                Long userId,
                                BigDecimal amount,
                                String method) {// WA) {
}
