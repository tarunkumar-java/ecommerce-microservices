package com.ecommerce.payment_service.dto;

import java.math.BigDecimal;

public record PaymentRequestDto(String orderId,
                                Long userId,
                                BigDecimal amount,
                                String method) {// WA) {
}
