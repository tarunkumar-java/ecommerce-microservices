package org.example;

import lombok.*;

import java.util.List;

public class ApiResponse<T> {

    private String status;
    private String message;
    private List<T> data;

    public ApiResponse() {}

    public ApiResponse(String status, String message, List<T> data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(String message, List<T> data) {
        return new ApiResponse<>("SUCCESS", message, data);
    }

    public static <T> ApiResponse<T> failure(String message) {
        return new ApiResponse<>("ERROR", message, null);
    }

    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public List<T> getData() { return data; }
}