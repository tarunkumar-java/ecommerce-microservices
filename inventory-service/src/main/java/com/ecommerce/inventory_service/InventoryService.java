package com.ecommerce.inventory_service;

import lombok.RequiredArgsConstructor;
import org.example.ApiResponse;
import org.example.InventoryReservedEvent;
import org.example.OrderCreatedEvent;
import org.example.PaymentCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryReservationRepository reservationRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final Logger log =
            LoggerFactory.getLogger(InventoryService.class);

    private static final String IDEMPOTENCY_PREFIX = "inventory:req:";

    // ===============================
    // 1️⃣ RESERVE STOCK
    // ===============================
    @KafkaListener(topics = "order-created", groupId = "inventory-group")
    @Transactional
    public void reserveStock(OrderCreatedEvent event) {

        log.info(
                "Received ORDER-CREATED event | orderId={} | productId={} | qty={} | occurredAt={}",
                event.orderId(), event.productId(), event.quantity(),event.occurredAt()
        );

        String redisKey =
                IDEMPOTENCY_PREFIX + "reserve:" + event.idempotencyKey();

        if (Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
            log.info(
                    "Duplicate RESERVE request skipped | idempotencyKey={}",
                    event.idempotencyKey()
            );
            return;
        }

        try {
            reservationRepository.findByIdempotencyKey(event.idempotencyKey())
                    .ifPresent(r -> {
                        log.info(
                                "Reservation already exists | inventoryId={} | orderId={}",
                                r.getInventoryId(), r.getOrderId()
                        );
                        throw new RuntimeException("Already reserved");
                    });

            InventoryEntity inventory = inventoryRepository
                    .findByProductIdForUpdate(Long.parseLong(event.productId()))
                    .orElseThrow(() -> {
                        log.error("Inventory NOT FOUND | productId={}", event.productId());
                        return new RuntimeException("Inventory not found");
                    });

            if (!inventory.isActive()) {
                log.info(
                        "Inventory INACTIVE | productId={} | orderId={}",
                        inventory.getProductId(), event.orderId()
                );

                /*kafkaTemplate.send(
                        "inventory-released",
                        event.orderId(),
                        event
                );*/
                return;
            }

            if (inventory.getAvailableQuantity() < event.quantity()) {
                log.info(
                        "INSUFFICIENT STOCK | productId={} | available={} | requested={}",
                        inventory.getProductId(),
                        inventory.getAvailableQuantity(),
                        event.quantity()
                );

                /*kafkaTemplate.send(
                        "inventory-released",
                        event.orderId(),
                        event
                );*/
                return;
            }

            inventory.setAvailableQuantity(
                    inventory.getAvailableQuantity() - event.quantity()
            );
            inventoryRepository.save(inventory);

            InventoryReservation reservation = new InventoryReservation();
            reservation.setInventoryId(generateInventoryId(event.orderId()));
            reservation.setOrderId(event.orderId());
            reservation.setProductId(Long.parseLong(event.productId()));
            reservation.setReservedQuantity(event.quantity());
            reservation.setIdempotencyKey(event.idempotencyKey());
            reservation.setStatus(InventoryStatus.RESERVED.toString());

            reservationRepository.save(reservation);

            InventoryReservedEvent inventoryReservedEvent = new InventoryReservedEvent(
                    event.userId(),
                    UUID.randomUUID().toString(),
                    reservation.getOrderId(),
                    reservation.getInventoryId(),
                    String.valueOf(reservation.getProductId()),
                    reservation.getReservedQuantity(),
                    reservation.getIdempotencyKey(),
                    Instant.now()
            );

            kafkaTemplate.send("inventory-reserved", reservation.getOrderId(), inventoryReservedEvent);


            log.info(
                    "INVENTORY RESERVED | inventoryId={} | orderId={} | qty={}",
                    reservation.getInventoryId(),
                    reservation.getOrderId(),
                    reservation.getReservedQuantity()
            );

        } finally {
            redisTemplate.opsForValue()
                    .set(redisKey, "DONE", Duration.ofHours(1));

            log.info(
                    "Reserve idempotency key stored in Redis | key={}",
                    redisKey
            );
        }
    }

    // ===============================
    // 2️⃣ RELEASE STOCK (payment failed)
    // ===============================
    @KafkaListener(topics = "payment-failed", groupId = "inventory-group")
    @Transactional
    public void releaseStock(PaymentCreatedEvent event) {

        log.info(
                "Received PAYMENT-FAILED event | orderId={} | paymentId={}",
                event.orderId(), event.paymentId()
        );

        String redisKey =
                IDEMPOTENCY_PREFIX + "release:" + event.idempotencyKey();

        if (Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
            log.info(
                    "Duplicate RELEASE request skipped | idempotencyKey={}",
                    event.idempotencyKey()
            );
            return;
        }

        try {
            InventoryReservation reservation =
                    reservationRepository
                            .findByIdempotencyKey(event.idempotencyKey())
                            .orElseThrow(() -> {
                                log.error(
                                        "Reservation NOT FOUND for release | idempotencyKey={}",
                                        event.idempotencyKey()
                                );
                                return new RuntimeException("Reservation not found");
                            });

            if ("RELEASED".equals(reservation.getStatus())) {
                log.info(
                        "Reservation already RELEASED | inventoryId={}",
                        reservation.getInventoryId()
                );
                return;
            }

            InventoryEntity inventory = inventoryRepository
                    .findByProductIdForUpdate(reservation.getProductId())
                    .orElseThrow(() -> new RuntimeException("Inventory not found"));

            inventory.setAvailableQuantity(
                    inventory.getAvailableQuantity()
                            + reservation.getReservedQuantity()
            );
            inventoryRepository.save(inventory);

            reservation.setStatus(InventoryStatus.RELEASED.toString());
            reservationRepository.save(reservation);

            log.info(
                    "INVENTORY RELEASED | inventoryId={} | orderId={}",
                    reservation.getInventoryId(),
                    reservation.getOrderId()
            );

        } finally {
            redisTemplate.opsForValue()
                    .set(redisKey, "DONE", Duration.ofHours(1));

            log.info(
                    "Release idempotency key stored in Redis | key={}",
                    redisKey
            );
        }
    }

    // ===============================
    // 3️⃣ CONFIRM STOCK (payment success)
    // ===============================
    @KafkaListener(topics = "payment-success", groupId = "inventory-group")
    @Transactional
    public void confirmStock(PaymentCreatedEvent event) {

        log.info(
                "Received PAYMENT-SUCCESS event | orderId={}",
                event.orderId()
        );

        String redisKey =
                IDEMPOTENCY_PREFIX + "confirm:" + event.idempotencyKey();

        if (Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
            log.info(
                    "Duplicate CONFIRM request skipped | idempotencyKey={}",
                    event.idempotencyKey()
            );
            return;
        }

        try {
            InventoryReservation reservation =
                    reservationRepository
                            .findByIdempotencyKey(event.idempotencyKey())
                            .orElseThrow(() -> new RuntimeException("Reservation not found"));

            if ("CONFIRMED".equals(reservation.getStatus())) {
                log.info(
                        "Reservation already CONFIRMED | inventoryId={}",
                        reservation.getInventoryId()
                );
                return;
            }

            reservation.setStatus(InventoryStatus.CONFIRMED.toString());
            reservationRepository.save(reservation);

            log.info(
                    "INVENTORY CONFIRMED | inventoryId={} | orderId={}",
                    reservation.getInventoryId(),
                    reservation.getOrderId()
            );

        } finally {
            redisTemplate.opsForValue()
                    .set(redisKey, "DONE", Duration.ofHours(1));

            log.info(
                    "Confirm idempotency key stored in Redis | key={}",
                    redisKey
            );
        }
    }

    // ===============================
    // 🔹 INVENTORY ID GENERATOR
    // ===============================
    private String generateInventoryId(String orderId) {
        return "INV-RES-" + orderId + "-" +
                UUID.randomUUID().toString().substring(0, 8);
    }



    @Transactional
    public ApiResponse<InventoryResponseDTO> getInventoryById(long productId) {
        Optional<InventoryEntity> inventoryEntity = inventoryRepository.findByProductIdForUpdate(productId);
        List<InventoryResponseDTO> dtoList=new ArrayList<>();
        String message="";
        if(inventoryEntity.isPresent()){
            dtoList.add(InventoryMapper.toDto(inventoryEntity.get()));
            message="Inventory Found";
        }
        return ApiResponse.
                success(message, dtoList);
    }
}
