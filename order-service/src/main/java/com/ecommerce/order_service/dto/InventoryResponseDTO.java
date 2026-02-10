package com.ecommerce.order_service.dto;

import lombok.Builder;

@Builder
public record InventoryResponseDTO(Long productId, int availableQuantity, int reservedQuantity, boolean active) {
}
