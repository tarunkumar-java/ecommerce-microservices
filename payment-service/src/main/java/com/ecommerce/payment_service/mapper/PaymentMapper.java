package com.ecommerce.payment_service.mapper;

import com.ecommerce.payment_service.dto.PaymentRequestDto;
import com.ecommerce.payment_service.entity.PaymentEntity;
import com.ecommerce.payment_service.enums.PaymentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public class PaymentMapper {

    public static PaymentEntity toEntity(PaymentRequestDto dto) {
        return PaymentEntity.builder()
                .paymentId("PAY_" + UUID.randomUUID())
                .orderId(dto.orderId())
                .userId(dto.userId())
                .amount(dto.amount())
                .method(dto.method())
                .status(PaymentStatus.INITIATED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
