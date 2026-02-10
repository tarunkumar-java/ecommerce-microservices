package org.example;

import java.io.Serializable;
import java.time.Instant;

public record OrderCreatedEvent(
        String eventId,          // unique event id (UUID)
        String orderId,          // business order id
        String userId,           // who placed the order
        String productId,        // what product
        int quantity,            // how many
        String idempotencyKey,   // request-level idempotency
        Instant occurredAt       // when event happened
) implements Serializable {}
