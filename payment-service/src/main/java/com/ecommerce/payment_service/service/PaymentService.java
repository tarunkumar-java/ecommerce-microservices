package com.ecommerce.payment_service.service;

import com.ecommerce.payment_service.client.ProductClient;
import com.ecommerce.payment_service.dto.ProductResponseDTO;
import com.ecommerce.payment_service.entity.PaymentEntity;
import com.ecommerce.payment_service.entity.WalletEntity;
import com.ecommerce.payment_service.entity.WalletTransactionEntity;
import com.ecommerce.payment_service.enums.PaymentStatus;
import com.ecommerce.payment_service.enums.TransactionType;
import com.ecommerce.payment_service.exception.WalletNotFoundException;
import com.ecommerce.payment_service.repo.PaymentRepository;
import com.ecommerce.payment_service.repo.WalletRepository;
import com.ecommerce.payment_service.repo.WalletTransactionRepository;
import com.ecommerce.payment_service.utility.PaymentUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ApiResponse;
import org.example.InventoryReservedEvent;
import org.example.PaymentCreatedEvent;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTxnRepository;
    private final PaymentRepository paymentRepository;
    private final ProductClient productClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String IDEMPOTENCY_KEY = "payment:req:";

    @KafkaListener(
            topics = "inventory-reserved",
            groupId = "payment-group"
    )
    @Transactional
    public void processPayment(InventoryReservedEvent event) {

        log.info(
                "InventoryReservedEvent received | orderId={} | productId={} | qty={} | occurredAt={}",
                event.orderId(),
                event.productId(),
                event.quantity(),
                event.occurredAt()
        );

        String redisKey = IDEMPOTENCY_KEY + event.orderId();

        // 1️⃣ Redis idempotency
        if (Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
            log.warn(
                    "Duplicate payment request skipped (Redis) | orderId={} | key={}",
                    event.orderId(),
                    redisKey
            );
            return;
        }

        // 2️⃣ DB idempotency
        paymentRepository.findByOrderId(event.orderId())
                .ifPresent(existing -> {
                    log.warn(
                            "Duplicate payment detected (DB) | orderId={} | paymentId={}",
                            event.orderId(),
                            existing.getPaymentId()
                    );
                    redisTemplate.opsForValue()
                            .set(redisKey, "DONE", Duration.ofHours(1));
                    throw new RuntimeException("Duplicate payment");
                });

        try {
            // 3️⃣ FETCH PRICE
            log.info(
                    "Fetching product price | productId={}",
                    event.productId()
            );

            ApiResponse<ProductResponseDTO> productResponse =
                    productClient.getProductById(Long.parseLong(event.productId()));

            if (!"SUCCESS".equals(productResponse.getStatus())
                    || productResponse.getData().isEmpty()) {

                log.error(
                        "Product price fetch failed | productId={} | orderId={}",
                        event.productId(),
                        event.orderId()
                );

                kafkaTemplate.send("payment-failed", event.orderId(), event);
                return;
            }

            double price = productResponse.getData().get(0).price();

            // 4️⃣ CALCULATE AMOUNT
            BigDecimal amount =
                    BigDecimal.valueOf(price)
                            .multiply(BigDecimal.valueOf(event.quantity()));

            log.info(
                    "Payment amount calculated | orderId={} | amount={}",
                    event.orderId(),
                    amount
            );

            String paymentId=PaymentUtility.generatePaymentId();
            // 5️⃣ CREATE PAYMENT
            PaymentEntity payment = PaymentEntity.builder()
                    .orderId(event.orderId())
                    .paymentId(paymentId)
                    .userId(Long.parseLong(event.userId()))
                    .amount(amount)
                    .status(PaymentStatus.PROCESSING)
                    .method("WALLET")
                    .createdAt(LocalDateTime.now())
                    .build();

            paymentRepository.save(payment);

            log.info(
                    "Payment created | orderId={} | paymentId={} | status=PROCESSING",
                    event.orderId(),
                    payment.getPaymentId()
            );

            // 6️⃣ LOCK WALLET
            WalletEntity wallet = walletRepository
                    .findByUserIdForUpdate(Long.parseLong(event.userId()))
                    .orElseThrow(() -> {
                        log.error(
                                "Wallet not found | userId={} | orderId={}",
                                event.userId(),
                                event.orderId()
                        );
                        return new WalletNotFoundException("Wallet not found");
                    });

            // 7️⃣ CHECK BALANCE
            if (wallet.getBalance().compareTo(amount) < 0) {

                log.warn(
                        "Insufficient balance | userId={} | balance={} | required={}",
                        event.userId(),
                        wallet.getBalance(),
                        amount
                );

                payment.setStatus(PaymentStatus.FAILED);
                paymentRepository.save(payment);

                kafkaTemplate.send("payment-failed", event.orderId(),
                        PaymentUtility.createPaymentEvent(PaymentStatus.FAILED.toString(),amount,paymentId,event));
                return;
            }

            // 8️⃣ DEDUCT BALANCE
            wallet.setBalance(wallet.getBalance().subtract(amount));
            walletRepository.save(wallet);

            log.info(
                    "Wallet debited | userId={} | amount={} | newBalance={}",
                    event.userId(),
                    amount,
                    wallet.getBalance()
            );

            // 9️⃣ WALLET TRANSACTION
            WalletTransactionEntity txn = WalletTransactionEntity.builder()
                    .userId(Long.parseLong(event.userId()))
                    .paymentId(payment.getPaymentId())
                    .amount(amount)
                    .type(TransactionType.DEBIT)
                    .createdAt(LocalDateTime.now())
                    .build();

            walletTxnRepository.save(txn);

            log.info(
                    "Wallet transaction created | paymentId={} | amount={}",
                    payment.getPaymentId(),
                    amount
            );

            // 🔟 SUCCESS
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setUpdatedAt(LocalDateTime.now());
            paymentRepository.save(payment);

            log.info(
                    "Payment SUCCESS | orderId={} | paymentId={}",
                    event.orderId(),
                    payment.getPaymentId()
            );

            kafkaTemplate.send("payment-success", event.orderId(),
                    PaymentUtility.createPaymentEvent(PaymentStatus.SUCCESS.toString(),amount,paymentId,event));

        } finally {
            redisTemplate.opsForValue()
                    .set(redisKey, "DONE", Duration.ofHours(1));

            log.info(
                    "Payment idempotency key stored | key={}",
                    redisKey
            );
        }
    }
}
