package com.ecommerce.inventory_service;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "inventory_reservation",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "inventoryId"),
                @UniqueConstraint(columnNames = "idempotencyKey"),
                @UniqueConstraint(columnNames = {"orderId", "productId"})
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // DB internal

    @Column(nullable = false, unique = true)
    private String inventoryId; // BUSINESS ID (INV-RES-xxx)

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private int reservedQuantity;

    @Column(nullable = false, unique = true)
    private String idempotencyKey;

    @Column(nullable = false)
    private String status; // RESERVED, CONFIRMED, RELEASED
}
