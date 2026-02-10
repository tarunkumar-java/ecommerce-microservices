package com.ecommerce.inventory_service;

import lombok.RequiredArgsConstructor;
import org.example.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService productService;

    /*@PostMapping
    public ApiResponse<InventoryResponseDTO> createProduct(@RequestBody InventoryRequestDTO request) {
        InventoryResponseDTO responseDTO=productService.createProduct(request);
        List<InventoryResponseDTO> apiResponses=new ArrayList<>();
        apiResponses.add(responseDTO);
        return ApiResponse.success("User created successfully", apiResponses);

    }*/

    @GetMapping("{productId}")
    public ApiResponse<InventoryResponseDTO> getInventoryById(@PathVariable long productId) {
        return productService.getInventoryById(productId);
    }

   /* @GetMapping
    public ApiResponse<InventoryResponseDTO> getAllProduct() {
        return productService.getAllUser();
    }

    @PostMapping("{productId}")
    public ApiResponse<InventoryResponseDTO> updateById(@PathVariable long productId){
        return productService.updateUser(productId);
    }

    @DeleteMapping(
            value = "/{userId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<InventoryResponseDTO>> deleteById(@PathVariable long productId){
        ApiResponse<InventoryResponseDTO> apiResponse= productService.deleteUserById(productId);
        return ResponseEntity.ok(apiResponse);

    }*/

   /* // Reserve stock
    @PostMapping("/reserve")
    public ResponseEntity<String> reserveStock(
            @RequestBody InventoryReserveRequest request) {

        productService.reserveStock(
                request.productId(),
                request.quantity()
        );
        return ResponseEntity.ok("Stock reserved");
    }

    // Release stock
    @PostMapping("/release")
    public ResponseEntity<String> releaseStock(
            @RequestBody InventoryReserveRequest request) {

        productService.releaseStock(
                request.productId(),
                request.quantity()
        );
        return ResponseEntity.ok("Stock released");
    }

    @PostMapping("/confirmStock")
    public ResponseEntity<String> confirmStock(
            @RequestBody InventoryReserveRequest request) {

        productService.confirmStock(
                request.productId(),
                request.quantity()
        );
        return ResponseEntity.ok("Stock confirm");
    }*/
}
