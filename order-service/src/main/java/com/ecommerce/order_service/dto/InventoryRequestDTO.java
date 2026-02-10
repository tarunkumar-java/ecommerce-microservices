package com.ecommerce.order_service.dto;

public record InventoryRequestDTO(Long productId, int availableQuantity, int reservedQuantity, boolean active) {

}
