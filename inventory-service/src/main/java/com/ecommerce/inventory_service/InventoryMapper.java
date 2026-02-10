package com.ecommerce.inventory_service;

import org.example.Utility;

public class InventoryMapper {

    public static InventoryEntity toEntity(InventoryRequestDTO requestDTO){
        InventoryEntity entity=new InventoryEntity();
        entity.setId(Utility.generate());
        entity.setAvailableQuantity(requestDTO.availableQuantity());
        entity.setActive(requestDTO.active());
        entity.setProductId(requestDTO.productId());
        return entity;
    }

    public static InventoryResponseDTO toDto(InventoryEntity entity){
        return InventoryResponseDTO.builder()
                .availableQuantity(entity.getAvailableQuantity())
                .productId(entity.getProductId())
                .active(entity.isActive())
                .productId(entity.getProductId())
                .build();
    }
}
