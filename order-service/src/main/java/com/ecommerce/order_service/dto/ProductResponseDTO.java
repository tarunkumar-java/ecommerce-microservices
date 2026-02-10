package com.ecommerce.order_service.dto;

import lombok.Builder;

@Builder
public record ProductResponseDTO(String name, String description, double price, boolean active) {
}
