package com.ecommerce.product_service;

import org.example.Utility;

public class ProductMapper {

    public static ProductEntity toEntity(ProductRequestDTO requestDTO){
        ProductEntity entity=new ProductEntity();
        entity.setId(Utility.generate());
        entity.setName(requestDTO.name());
        entity.setPrice(requestDTO.price());
        entity.setActive(requestDTO.active());
        entity.setDescription(requestDTO.description());
        return entity;
    }

    public static ProductResponseDTO toDto(ProductEntity entity){
        return ProductResponseDTO.builder()
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .active(entity.isActive())
                .build();
    }
}
