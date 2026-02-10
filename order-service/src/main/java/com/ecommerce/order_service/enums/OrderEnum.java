package com.ecommerce.order_service.enums;

public enum OrderEnum {
    CREATED,            // order created, validation pending
    VALIDATED,          // user + product validated
    INVENTORY_RESERVED, // stock reserved
    PAYMENT_PENDING,    // waiting for payment
    CONFIRMED,          // payment success
    CANCELLED,          // failed / user invalid / out of stock
    FAILED,             // technical failure
    REFUNDED
}
