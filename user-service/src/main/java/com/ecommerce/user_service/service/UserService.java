package com.ecommerce.user_service.service;

import com.ecommerce.user_service.dto.UserRequestDTO;
import com.ecommerce.user_service.dto.UserResponseDTO;
import com.ecommerce.user_service.entity.UserEntity;
import com.ecommerce.user_service.mapper.UserMapper;
import com.ecommerce.user_service.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.example.ApiResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    List<UserResponseDTO> userResponseDTOArrayList=new ArrayList<>();

    public UserResponseDTO createUser(UserRequestDTO userRequestDTO){
        UserEntity entity = UserMapper.toEntity(userRequestDTO);
        UserEntity createdUser = userRepository.save(entity);
        return UserMapper.toUserResponseDTO(createdUser);
    }

    public ApiResponse<UserResponseDTO> getAllUser() {
        List<UserResponseDTO> users = userRepository.findAll()
                .stream()
                .map(UserMapper::toUserResponseDTO)
                .toList();
        if (users.isEmpty()) {
            return ApiResponse.failure("No users found");
        }
        return ApiResponse.success("Users fetched successfully", users);
    }


    public ApiResponse<UserResponseDTO> getAllUserById(int userId){
        userResponseDTOArrayList.clear();
        Optional<UserEntity> userEntity = userRepository.findById(userId);
        if(userEntity.isPresent()){
            userResponseDTOArrayList.add(UserMapper.toUserResponseDTO(userEntity.get()));
            return ApiResponse.success("User Fetch Successfully", userResponseDTOArrayList);
        }
        return ApiResponse.failure("User not found");
    }

    public ApiResponse<UserResponseDTO> updateUser(UserRequestDTO requestDTO){
        userResponseDTOArrayList.clear();
        Optional<UserEntity> userEntity = userRepository.findByEmailId(requestDTO.emailId());
        if(userEntity.isPresent()){
            userResponseDTOArrayList.add(UserMapper.toUserResponseDTO(userRepository.save(UserMapper.toEntity(requestDTO))));
            return ApiResponse.success("User Updated successfully", userResponseDTOArrayList);
        }
        return ApiResponse.failure("User not found");
    }

    public ApiResponse<UserResponseDTO> deleteUserById(int userId) {
        Optional<UserEntity> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return ApiResponse.failure("User not found");
        }
        userRepository.deleteById(userId);
        return ApiResponse.success("User deleted successfully", List.of());
    }
}
