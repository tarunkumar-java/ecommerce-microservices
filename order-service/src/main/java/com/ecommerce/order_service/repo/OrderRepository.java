package com.ecommerce.order_service.repo;

import com.ecommerce.order_service.entity.OrderEntity;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity,Integer> {
    Optional<OrderEntity> findByOrderId(long orderId);
    Optional<OrderEntity> findByUserId(int userId);
    Optional<OrderEntity> findByIdempotencyId(String findByIdempotencyId);
}
