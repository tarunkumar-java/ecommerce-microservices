package com.ecommerce.order_service.dto;

public record InventoryReserveRequest(Long productId, int quantity) {
}
