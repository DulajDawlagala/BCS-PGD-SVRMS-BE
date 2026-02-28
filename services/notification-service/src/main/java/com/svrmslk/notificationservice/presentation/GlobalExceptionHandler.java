// com/svrmslk/notificationservice/presentation/GlobalExceptionHandler.java
package com.svrmslk.notificationservice.presentation;

import com.svrmslk.notificationservice.common.security.TenantIsolationViolationException;
import com.svrmslk.notificationservice.common.security.TenantValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(TenantIsolationViolationException.class)
    public ResponseEntity<ErrorResponse> handleTenantIsolationViolation(TenantIsolationViolationException e) {
        logger.error("Tenant isolation violation detected", e);
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse("TENANT_ISOLATION_VIOLATION", "Access forbidden"));
    }

    @ExceptionHandler(TenantValidationException.class)
    public ResponseEntity<ErrorResponse> handleTenantValidation(TenantValidationException e) {
        logger.error("Tenant validation failed", e);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("TENANT_VALIDATION_ERROR", e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e) {
        logger.warn("Invalid request parameter", e);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("INVALID_REQUEST", e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
        logger.error("Unexpected error occurred", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"));
    }

    private record ErrorResponse(String code, String message) {
    }
}