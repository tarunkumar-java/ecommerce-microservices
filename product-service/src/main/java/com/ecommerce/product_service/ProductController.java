package com.ecommerce.product_service;

import lombok.RequiredArgsConstructor;
import org.example.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ApiResponse<ProductResponseDTO> createProduct(@RequestBody ProductRequestDTO request) {
        ProductResponseDTO responseDTO=productService.createProduct(request);
        List<ProductResponseDTO> apiResponses=new ArrayList<>();
        apiResponses.add(responseDTO);
        return ApiResponse.success("User created successfully", apiResponses);

    }

    @GetMapping("{productId}")
    public ApiResponse<ProductResponseDTO> getProductById(@PathVariable long productId) {
        return productService.getAllUserById(productId);
    }

    @GetMapping
    public ApiResponse<ProductResponseDTO> getAllProduct() {
        return productService.getAllUser();
    }

    @PostMapping("{productId}")
    public ApiResponse<ProductResponseDTO> updateById(@PathVariable long productId){
        return productService.updateUser(productId);
    }

    @DeleteMapping(
            value = "/{userId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<ProductResponseDTO>> deleteById(@PathVariable long productId){
        ApiResponse<ProductResponseDTO> apiResponse= productService.deleteUserById(productId);
        return ResponseEntity.ok(apiResponse);

    }
}
