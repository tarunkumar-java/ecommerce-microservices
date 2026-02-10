package com.ecommerce.product_service;

import lombok.Builder;

@Builder
public record ProductResponseDTO(String name,String description,double price,boolean active) {
}
