package com.ecommerce.user_service.repo;

import com.ecommerce.user_service.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity,Integer> {

    Optional<UserEntity> findByEmailId(String emailId);

}
