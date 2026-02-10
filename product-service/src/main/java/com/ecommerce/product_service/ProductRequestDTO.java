package com.ecommerce.product_service;

import jakarta.persistence.Column;

public record ProductRequestDTO(String name,String description,double price,boolean active) {
}
