package com.ecommerce.user_service.mapper;

import com.ecommerce.user_service.dto.UserRequestDTO;
import com.ecommerce.user_service.dto.UserResponseDTO;
import com.ecommerce.user_service.entity.UserEntity;
import com.ecommerce.user_service.enums.UserStatus;
import jakarta.validation.constraints.NotNull;

import java.util.Random;
import java.util.UUID;

public class UserMapper {

    public static UserEntity toEntity(@NotNull UserRequestDTO requestDTO){
        return UserEntity.builder()
                .emailId(requestDTO.emailId())
                .phoneNo(requestDTO.phoneNo())
                .userStatus(UserStatus.valueOf(requestDTO.userStatus()))
                .firstName(requestDTO.firstName())
                .lastName(requestDTO.lastName())
                .userId(new Random().nextInt())
                .build();
    }

    public static UserResponseDTO toUserResponseDTO(@NotNull UserEntity entity){
       return UserResponseDTO.builder()
               .emailId(entity.getEmailId())
               .firstName(entity.getFirstName())
               .lastName(entity.getLastName())
               .phoneNo(entity.getPhoneNo())
               .userStatus(String.valueOf(String.valueOf(entity.getUserStatus())))
               .build();
    }
}
