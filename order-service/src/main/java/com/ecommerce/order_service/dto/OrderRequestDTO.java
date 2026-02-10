package com.ecommerce.order_service.dto;

public record OrderRequestDTO(int userId,int productId,int quantity,String idempotencyId) {
}
