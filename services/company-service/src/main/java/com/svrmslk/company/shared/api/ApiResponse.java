package com.svrmslk.company.shared.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private String message;
    private String error;
    private Instant timestamp;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null, null, Instant.now());
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, data, message, null, Instant.now());
    }

    public static <T> ApiResponse<T> error(String error) {
        return new ApiResponse<>(false, null, null, error, Instant.now());
    }
}