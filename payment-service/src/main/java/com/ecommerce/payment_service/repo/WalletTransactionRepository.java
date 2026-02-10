package com.ecommerce.payment_service.repo;

import com.ecommerce.payment_service.entity.WalletTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransactionEntity, Long> {

    List<WalletTransactionEntity> findByUserId(Long userId);

    List<WalletTransactionEntity> findByPaymentId(String paymentId);
}
