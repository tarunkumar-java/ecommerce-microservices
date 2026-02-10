package com.ecommerce.order_service.entity;

import org.springframework.stereotype.Component;

@Component
public class KafkaTopics {
    public static final String ORDER_CREATED = "order-created";
    public static final String INVENTORY_RESERVED = "inventory-reserved";
    public static final String INVENTORY_RELEASED = "inventory-released";
    public static final String PAYMENT_SUCCESS = "payment-success";
    public static final String PAYMENT_FAILED = "payment-failed";
}
