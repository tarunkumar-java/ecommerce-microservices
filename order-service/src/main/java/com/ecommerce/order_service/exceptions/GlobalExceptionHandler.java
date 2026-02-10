package com.ecommerce.order_service.exceptions;

import com.ecommerce.order_service.exceptions.DuplicateOrderException;
import com.ecommerce.order_service.exceptions.PaymentFailedException;
import com.ecommerce.order_service.exceptions.ProductNotFoundException;
import com.ecommerce.order_service.exceptions.UserNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import org.example.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> userNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.failure(ex.getMessage()));
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> productNotFound(ProductNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.failure(ex.getMessage()));
    }

    @ExceptionHandler(PaymentFailedException.class)
    public ResponseEntity<ApiResponse<?>> paymentFailed(PaymentFailedException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.failure(ex.getMessage()));
    }

    @ExceptionHandler(DuplicateOrderException.class)
    public ResponseEntity<ApiResponse<?>> duplicateOrder(DuplicateOrderException ex) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.failure(ex.getMessage()));
    }

    @ExceptionHandler(InventoryException.class)
    public ResponseEntity<ApiResponse<?>> duplicateOrder(InventoryException ex) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.failure(ex.getMessage()));
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ApiResponse<?>> handleFeignException(FeignException ex) {
        try {
            ApiResponse<?> error =
                    objectMapper.readValue(ex.contentUTF8(), ApiResponse.class);

            return ResponseEntity
                    .status(ex.status())
                    .body(ApiResponse.failure(error.getMessage()));

        } catch (Exception e) {
            // fallback if parsing fails
            return ResponseEntity
                    .status(ex.status())
                    .body(ApiResponse.failure("Payment service error"));
        }
    }

    @ExceptionHandler(feign.RetryableException.class)
    public ResponseEntity<ApiResponse<?>> handleRetryable(feign.RetryableException ex) {
        return ResponseEntity.status(503)
                .body(ApiResponse.failure("Payment service is unavailable"));
    }

    @ExceptionHandler(CallNotPermittedException.class)
    public ResponseEntity<ApiResponse<?>> handleCircuitOpen(CallNotPermittedException ex) {
        return ResponseEntity.status(503)
                .body(ApiResponse.failure("Payment service temporarily unavailable"));
    }

    @ExceptionHandler(CompletionException.class)
    public ResponseEntity<ApiResponse<?>> handleCompletionException(CompletionException ex) {
        Throwable cause = ex.getCause();

        if (cause instanceof FeignException fe) {
            return handleFeignException(fe);
        }

        return ResponseEntity.internalServerError()
                .body(ApiResponse.failure("Something went wrong"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> generic(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.failure("Something went wrong"));
    }


}

