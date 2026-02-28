// FILE: shared/api/ApiResponse.java
        package com.svrmslk.customer.shared.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private Integer status;
    private LocalDateTime timestamp;

    private ApiResponse(boolean success, String message, T data, Integer status) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, null, data, 200);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, 200);
    }

    public static <T> ApiResponse<T> error(String message, Integer status) {
        return new ApiResponse<>(false, message, null, status);
    }

    public static <T> ApiResponse<T> error(String message, Integer status, T data) {
        return new ApiResponse<>(false, message, data, status);
    }

    // Getters
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public T getData() { return data; }
    public Integer getStatus() { return status; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
