package org.example;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

public record PaymentCreatedEvent(
        String eventId,          // unique event id
        String orderId,          // saga correlation id
        String paymentId,        // business payment id
        String userId,
        BigDecimal amount,
        String idempotencyKey,
        Instant occurredAt,
        String paymentStatus
) implements Serializable {}
