package com.ecommerce.order_service.entity;

import com.ecommerce.order_service.enums.OrderEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name="orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private long orderId;
    private int userId;
    private int productId;
    private int quantity;
    private int price;
    @Enumerated(EnumType.STRING)
    private OrderEnum orderStatus;
    // 🔥 NEW FIELD (idempotency)
    @Column(name = "idempotency_id", nullable = false, unique = true, length = 100)
    private String idempotencyId;

}
