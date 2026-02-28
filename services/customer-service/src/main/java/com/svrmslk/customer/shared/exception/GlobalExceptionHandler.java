/// / FILE: shared/exception/GlobalExceptionHandler.java
//        package com.svrmslk.customer.shared.exception;
//
//import com.svrmslk.customer.shared.api.ApiResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.AccessDeniedException;
//import org.springframework.validation.FieldError;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//import java.time.LocalDateTime;
//import java.util.HashMap;
//import java.util.Map;
//
//@RestControllerAdvice
//@Slf4j
//public class GlobalExceptionHandler {
//
//    @ExceptionHandler(CustomerNotFoundException.class)
//    public ResponseEntity<ApiResponse<Void>> handleCustomerNotFound(CustomerNotFoundException ex) {
//        log.warn("Customer not found: {}", ex.getMessage());
//        return ResponseEntity
//                .status(HttpStatus.NOT_FOUND)
//                .body(ApiResponse.error(ex.getMessage(), HttpStatus.NOT_FOUND.value()));
//    }
//
//    @ExceptionHandler(CustomerAlreadyExistsException.class)
//    public ResponseEntity<ApiResponse<Void>> handleCustomerAlreadyExists(CustomerAlreadyExistsException ex) {
//        log.warn("Customer already exists: {}", ex.getMessage());
//        return ResponseEntity
//                .status(HttpStatus.CONFLICT)
//                .body(ApiResponse.error(ex.getMessage(), HttpStatus.CONFLICT.value()));
//    }
//
//    @ExceptionHandler(ValidationException.class)
//    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidation(ValidationException ex) {
//        log.warn("Validation error: {}", ex.getMessage());
//        return ResponseEntity
//                .status(HttpStatus.BAD_REQUEST)
//                .body(ApiResponse.error(ex.getMessage(), HttpStatus.BAD_REQUEST.value(), ex.getErrors()));
//    }
//
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<ApiResponse<Map<String, String>>> handleMethodArgumentNotValid(
//            MethodArgumentNotValidException ex) {
//        Map<String, String> errors = new HashMap<>();
//        ex.getBindingResult().getAllErrors().forEach(error -> {
//            String fieldName = ((FieldError) error).getField();
//            String errorMessage = error.getDefaultMessage();
//            errors.put(fieldName, errorMessage);
//        });
//
//        log.warn("Validation error: {}", errors);
//        return ResponseEntity
//                .status(HttpStatus.BAD_REQUEST)
//                .body(ApiResponse.error("Validation failed", HttpStatus.BAD_REQUEST.value(), errors));
//    }
//
//    @ExceptionHandler(AccessDeniedException.class)
//    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException ex) {
//        log.error("Access denied: {}", ex.getMessage());
//        return ResponseEntity
//                .status(HttpStatus.FORBIDDEN)
//                .body(ApiResponse.error("Access denied", HttpStatus.FORBIDDEN.value()));
//    }
//
//    @ExceptionHandler(SecurityException.class)
//    public ResponseEntity<ApiResponse<Void>> handleSecurity(SecurityException ex) {
//        log.error("Security error: {}", ex.getMessage());
//        return ResponseEntity
//                .status(HttpStatus.FORBIDDEN)
//                .body(ApiResponse.error(ex.getMessage(), HttpStatus.FORBIDDEN.value()));
//    }
//
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ApiResponse<Void>> handleGeneric(Exception ex) {
//        log.error("Unexpected error", ex);
//        return ResponseEntity
//                .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body(ApiResponse.error("An unexpected error occurred",
//                        HttpStatus.INTERNAL_SERVER_ERROR.value()));
//    }
//}


package com.svrmslk.customer.shared.exception;

import com.svrmslk.customer.shared.api.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidation(MethodArgumentNotValidException ex) {
        var errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(e -> e.getField(), e -> e.getDefaultMessage()));
        return ResponseEntity.badRequest().body(ApiResponse.error("Validation Failed", 400, errors));
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ApiResponse<Void>> handleSecurity(SecurityException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(ex.getMessage(), 401));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccess(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error("Forbidden", 403));
    }

    @ExceptionHandler(CustomerAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Void>> handleExists(CustomerAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error(ex.getMessage(), 409));
    }
}