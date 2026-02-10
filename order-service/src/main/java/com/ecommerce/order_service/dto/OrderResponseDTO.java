package com.ecommerce.order_service.dto;

import com.ecommerce.order_service.enums.OrderEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Data;

@Builder
public record OrderResponseDTO(long orderId,int userId,int productId,int quantity,int price,OrderEnum orderStatus) {
}
