package com.ecommerce.inventory_service;

import lombok.Builder;

@Builder
public record InventoryResponseDTO(Long productId, int availableQuantity, boolean active) {
}
