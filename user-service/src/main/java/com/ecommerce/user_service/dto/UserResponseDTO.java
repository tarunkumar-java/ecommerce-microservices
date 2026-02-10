package com.ecommerce.user_service.dto;

import lombok.Builder;

@Builder
public record UserResponseDTO(String firstName,String lastName,String emailId,String phoneNo,String userStatus) {
}
