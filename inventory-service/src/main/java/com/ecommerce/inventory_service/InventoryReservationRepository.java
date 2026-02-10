package com.ecommerce.inventory_service;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryReservationRepository extends JpaRepository<InventoryReservation,Long> {
    Optional<InventoryReservation> findByIdempotencyKey(String idempotencyKey);
    Optional<InventoryReservation> findByInventoryId(String inventoryId);
}
