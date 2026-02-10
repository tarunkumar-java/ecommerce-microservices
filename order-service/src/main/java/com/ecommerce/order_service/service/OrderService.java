package com.ecommerce.order_service.service;

import com.ecommerce.order_service.dto.*;
import com.ecommerce.order_service.entity.OrderEntity;
import com.ecommerce.order_service.enums.OrderEnum;
import com.ecommerce.order_service.gateway.InventoryGateway;
import com.ecommerce.order_service.gateway.ProductGateway;
import com.ecommerce.order_service.gateway.UserGateway;
import com.ecommerce.order_service.mapper.OrderMapper;
import com.ecommerce.order_service.repo.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.example.ApiResponse;
import org.example.InventoryReservedEvent;
import org.example.OrderCreatedEvent;
import org.example.PaymentCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserGateway userGateway;
    private final ProductGateway productGateway;
    private final InventoryGateway inventoryGateway;

    private static final String IDEMPOTENCY_KEY = "order:req:";
    private static final String SAGA_KEY = "order:saga:";

    private static final Logger log =
            LoggerFactory.getLogger(OrderService.class);

    // ===============================
    // 1️⃣ CREATE ORDER
    // ===============================
    @Transactional
    public ApiResponse<OrderResponseDTO> createOrder(OrderRequestDTO request) {

        log.info(
                "Create order request received | userId={} | productId={} | qty={} | idempotencyId={}",
                request.userId(),
                request.productId(),
                request.quantity(),
                request.idempotencyId()
        );

        //Basic Validation to process order like stock available, user exist etc.
        ApiResponse<UserResponseDTO> userFeignResponse=userGateway.getUser((long) request.userId());
        if(userFeignResponse.getData()==null){
            return ApiResponse.failure(
                    "Please enter valid user id");
        }else if(userFeignResponse.getStatus().equals("ERROR")){
            return ApiResponse.failure(userFeignResponse.getMessage());
        }

        ApiResponse<ProductResponseDTO> productFeignResponse=productGateway.getProduct((long) request.productId());
        if(productFeignResponse.getData()==null){
            return ApiResponse.failure(
                    "Please enter valid product id");
        }else if(productFeignResponse.getStatus().equals("ERROR")){
            return ApiResponse.failure(productFeignResponse.getMessage());
        }

        ApiResponse<InventoryResponseDTO> feignInventoryResponse=inventoryGateway.getInventory((long) request.productId());
        if(feignInventoryResponse.getData()!=null){
            InventoryResponseDTO responseDTO=feignInventoryResponse.getData().get(0);
            if(responseDTO.availableQuantity() < request.quantity())  {
                return ApiResponse.failure(
                        "Out of Stock"
                );
            }
        }else if(feignInventoryResponse.getStatus().equals("ERROR")){
            return ApiResponse.failure(feignInventoryResponse.getMessage());
        }

        // 1️⃣ Idempotency check (Redis)
        String idempotencyKey = IDEMPOTENCY_KEY + request.idempotencyId();
        if (Boolean.TRUE.equals(redisTemplate.hasKey(idempotencyKey))) {

            log.info(
                    "Duplicate order request detected | idempotencyId={}",
                    request.idempotencyId()
            );

            Optional<OrderEntity> existing =
                    orderRepository.findByIdempotencyId(request.idempotencyId());

            if (existing.isPresent()) {
                log.info(
                        "Returning existing order | orderId={} | status={}",
                        existing.get().getOrderId(),
                        existing.get().getOrderStatus()
                );

                return ApiResponse.success(
                        "Order already processed",
                        List.of(OrderMapper.todto(existing.get()))
                );
            }
        }

        // 2️⃣ Create Order
        OrderEntity order = OrderMapper.toEntity(request);
        order.setOrderStatus(OrderEnum.CREATED);
        orderRepository.save(order);

        log.info(
                "Order CREATED | orderId={} | status={} | createdAt={}",
                order.getOrderId(),
                order.getOrderStatus(),
                Instant.now()

        );

        // 3️⃣ Save saga state
        redisTemplate.opsForValue()
                .set(
                        SAGA_KEY + order.getOrderId(),
                        "CREATED",
                        Duration.ofHours(1)
                );

        log.info(
                "Saga state set to CREATED | orderId={}",
                order.getOrderId()
        );


        // 4️⃣ Publish OrderCreated event
        OrderCreatedEvent event = new OrderCreatedEvent(
                UUID.randomUUID().toString(),
                String.valueOf(order.getOrderId()),
                String.valueOf(order.getUserId()),
                String.valueOf(order.getProductId()),
                order.getQuantity(),
                request.idempotencyId(),
                Instant.now()
        );

        kafkaTemplate.send(
                "order-created",
                String.valueOf(order.getOrderId()),
                event
        );

        log.info(
                "OrderCreated event published | orderId={} | eventId={} | occurredAt={}",
                order.getOrderId(),
                event.eventId(),
                event.occurredAt()
        );

        // 5️⃣ Mark idempotency key
        redisTemplate.opsForValue()
                .set(idempotencyKey, "DONE", Duration.ofMinutes(10));

        log.info(
                "Idempotency key stored | key={}",
                idempotencyKey
        );

        return ApiResponse.success(
                "Order request accepted",
                List.of(OrderMapper.todto(order))
        );
    }

    // ===============================
    // 🔥 SAGA LISTENERS
    // ===============================

    @Transactional
    @org.springframework.kafka.annotation.KafkaListener(
            topics = "inventory-reserved",
            groupId = "order-group"
    )
    public void onInventoryReserved(InventoryReservedEvent event) {

        log.info(
                "Inventory RESERVED event received | orderId={} | occurredAt={}",
                event.orderId(),
                event.occurredAt()
        );

        OrderEntity order = orderRepository
                .findByOrderId(Long.parseLong(event.orderId()))
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setOrderStatus(OrderEnum.INVENTORY_RESERVED);
        orderRepository.save(order);

        redisTemplate.opsForValue()
                .set(
                        SAGA_KEY + order.getOrderId(),
                        "INVENTORY_RESERVED",
                        Duration.ofHours(1)
                );

        log.info(
                "Order status updated to INVENTORY_RESERVED | orderId={}",
                order.getOrderId()
        );
    }

    @Transactional
    @org.springframework.kafka.annotation.KafkaListener(
            topics = "payment-success",
            groupId = "order-group"
    )
    public void onPaymentSuccess(PaymentCreatedEvent event) {

        log.info(
                "Payment SUCCESS event received | orderId={} | occurredAt={}",
                event.orderId(),
                event.occurredAt()
        );

        OrderEntity order = orderRepository
                .findByOrderId(Long.parseLong(event.orderId()))
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setOrderStatus(OrderEnum.CONFIRMED);
        orderRepository.save(order);

        redisTemplate.opsForValue()
                .set(
                        SAGA_KEY + order.getOrderId(),
                        "CONFIRMED",
                        Duration.ofHours(1)
                );

        log.info(
                "Order CONFIRMED | orderId={} | occurredAt={}",
                order.getOrderId(),
                Instant.now()
        );
    }

    @Transactional
    @org.springframework.kafka.annotation.KafkaListener(
            topics = "payment-failed",
            groupId = "order-group"
    )
    public void onPaymentFailed(PaymentCreatedEvent event) {

        log.info(
                "Payment FAILED event received | orderId={} | occurredAt={}",
                event.orderId(),
                event.occurredAt()
        );

        OrderEntity order = orderRepository
                .findByOrderId(Long.parseLong(event.orderId()))
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setOrderStatus(OrderEnum.CANCELLED);
        orderRepository.save(order);

        redisTemplate.opsForValue()
                .set(
                        SAGA_KEY + order.getOrderId(),
                        "FAILED",
                        Duration.ofHours(1)
                );

        log.info(
                "Order CANCELLED | orderId={} | occurredAt={}",
                order.getOrderId(),
                Instant.now()
        );
    }

    // ===============================
    // 🔎 GET ORDER
    // ===============================
    public OrderResponseDTO getOrder(String orderId) {

        log.info("Get order request | orderId={}", orderId);

        String sagaState =
                (String) redisTemplate.opsForValue()
                        .get(SAGA_KEY + orderId);

        log.info(
                "Saga state from Redis | orderId={} | sagaState={}",
                orderId,
                sagaState
        );

        OrderEntity order = orderRepository
                .findByOrderId(Long.parseLong(orderId))
                .orElseThrow(() -> new RuntimeException("Order not found"));

        log.info(
                "Order fetched from DB | orderId={} | status={}",
                order.getOrderId(),
                order.getOrderStatus()
        );

        return OrderMapper.todto(order);
    }
}
