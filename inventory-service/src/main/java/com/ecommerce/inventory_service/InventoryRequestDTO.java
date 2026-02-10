package com.ecommerce.inventory_service;

public record InventoryRequestDTO(Long productId, int availableQuantity, int reservedQuantity, boolean active) {

}
