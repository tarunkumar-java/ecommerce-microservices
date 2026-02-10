package com.ecommerce.payment_service.dto;

import lombok.Builder;

@Builder
public record ProductResponseDTO(String name, String description, double price, boolean active) {
}
