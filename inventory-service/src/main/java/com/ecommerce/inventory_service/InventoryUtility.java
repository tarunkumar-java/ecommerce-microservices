package com.ecommerce.inventory_service;

import java.util.UUID;

public class InventoryUtility {
    public static String generateInventoryId(String orderId) {
        return "INV-RES-" +
                orderId +
                "-" +
                UUID.randomUUID().toString().substring(0, 8);
    }
}
