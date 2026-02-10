package org.example;

import java.io.Serializable;import java.time.Instant;public record InventoryReservedEvent(
        String userId,
        String eventId,          // unique event id
        String orderId,          // saga correlation id
        String inventoryId,      // business inventory reservation id
        String productId,
        int quantity,
        String idempotencyKey,
        Instant occurredAt
) implements Serializable {}
