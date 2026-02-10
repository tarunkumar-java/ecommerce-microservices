package com.ecommerce.user_service.controller;

import com.ecommerce.user_service.dto.UserRequestDTO;
import com.ecommerce.user_service.dto.UserResponseDTO;
import com.ecommerce.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.example.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping
    public ApiResponse<UserResponseDTO> create(@RequestBody UserRequestDTO request) {
        UserResponseDTO responseDTO=service.createUser(request);
        List<UserResponseDTO> apiResponses=new ArrayList<>();
        apiResponses.add(responseDTO);
        return ApiResponse.success("User created successfully", apiResponses);

    }

    @GetMapping("{userId}")
    public ApiResponse<UserResponseDTO> getUserById(@PathVariable int userId) {
        return service.getAllUserById(userId);
    }

    @GetMapping
    public ApiResponse<UserResponseDTO> getAllUser() {
        return service.getAllUser();
    }

    @PostMapping("{userId}")
    public ApiResponse<UserResponseDTO> updateById(UserRequestDTO requestDTO){
        return service.updateUser(requestDTO);
    }

    @DeleteMapping(
            value = "/{userId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<UserResponseDTO>> deleteById(@PathVariable int userId){
         ApiResponse<UserResponseDTO> apiResponse= service.deleteUserById(userId);
         return ResponseEntity.ok(apiResponse);

    }
}

