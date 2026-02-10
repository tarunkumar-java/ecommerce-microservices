package com.ecommerce.product_service;

import lombok.RequiredArgsConstructor;
import org.example.ApiResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    private List<ProductResponseDTO> responseDTO = new ArrayList<>();

    public ProductResponseDTO createProduct(ProductRequestDTO userRequestDTO){
        ProductEntity createdUser = productRepository.save(ProductMapper.toEntity(userRequestDTO));
        return ProductMapper.toDto(createdUser);
    }

    public ApiResponse<ProductResponseDTO> getAllUser() {
        List<ProductResponseDTO> users = productRepository.findAll()
                .stream()
                .map(ProductMapper::toDto)
                .toList();
        if (users.isEmpty()) {
            return ApiResponse.failure("No product found");
        }
        return ApiResponse.success("Products fetched successfully", users);
    }


    public ApiResponse<ProductResponseDTO> getAllUserById(long productId){
        responseDTO.clear();
        Optional<ProductEntity> userEntity = productRepository.findById(productId);
        if(userEntity.isPresent()){
            responseDTO.add(ProductMapper.toDto(userEntity.get()));
            return ApiResponse.success("Product Fetch Successfully", responseDTO);
        }
        return ApiResponse.failure("Product not found");
    }

    public ApiResponse<ProductResponseDTO> updateUser(long productId){
        responseDTO.clear();
        Optional<ProductEntity> userEntity = productRepository.findById(productId);
        if(userEntity.isPresent()){
            responseDTO.add(ProductMapper.toDto(productRepository.save(userEntity.get())));
            return ApiResponse.success("Product Updated successfully", responseDTO);
        }
        return ApiResponse.failure("Product not found");
    }

    public ApiResponse<ProductResponseDTO> deleteUserById(long productId) {
        Optional<ProductEntity> user = productRepository.findById(productId);
        if (user.isEmpty()) {
            return ApiResponse.failure("User not found");
        }
        productRepository.deleteById(productId);
        return ApiResponse.success("Product deleted successfully", List.of());
    }
}
