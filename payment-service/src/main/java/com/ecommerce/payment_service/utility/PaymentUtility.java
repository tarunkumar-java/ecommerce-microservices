package com.ecommerce.payment_service.utility;

import org.example.InventoryReservedEvent;
import org.example.PaymentCreatedEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;


public class PaymentUtility {
    public static String generatePaymentId() {
        return "PAY-" +
                System.currentTimeMillis() +
                "-" +
                ThreadLocalRandom.current().nextInt(1000, 9999);
    }

    public static PaymentCreatedEvent createPaymentEvent(String paymentStatus, BigDecimal amount,String paymentId,InventoryReservedEvent inventoryReservedEvent){
       return  new PaymentCreatedEvent(
               inventoryReservedEvent.eventId(),          // unique event id
               inventoryReservedEvent.orderId(),          // saga correlation id
               paymentId,        // business payment id
               inventoryReservedEvent.userId(),
               amount,
               inventoryReservedEvent.idempotencyKey(),
               Instant.now(),
               paymentStatus
        );
    }
}
