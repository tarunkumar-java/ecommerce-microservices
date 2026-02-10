package com.ecommerce.order_service.mapper;

import com.ecommerce.order_service.dto.OrderRequestDTO;
import com.ecommerce.order_service.dto.OrderResponseDTO;
import com.ecommerce.order_service.entity.OrderEntity;
import com.ecommerce.order_service.enums.OrderEnum;
import com.ecommerce.order_service.utility.OrderUtility;
import org.example.Utility;

import java.util.UUID;

public class OrderMapper {

    public static OrderEntity toEntity(OrderRequestDTO requestDTO){
        OrderEntity order = new OrderEntity();
        order.setOrderId(Utility.generate()); // business order id
        order.setProductId(requestDTO.productId());
        order.setUserId(requestDTO.userId());
        order.setIdempotencyId(requestDTO.idempotencyId());
        order.setQuantity(requestDTO.quantity());
        order.setOrderStatus(OrderEnum.CREATED);
        return order;
    }

    public static OrderResponseDTO todto(OrderEntity entity){
        return OrderResponseDTO.builder()
                .userId(entity.getUserId())
                .productId(entity.getProductId())
                .quantity(entity.getQuantity())
                .orderStatus(entity.getOrderStatus())
                .price(entity.getPrice())
                .orderId(entity.getOrderId())
                .build();
    }
}
