//package com.svrmslk.company.shared.exception;
//
//import com.svrmslk.company.shared.api.ApiResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
//@RestControllerAdvice
//@Slf4j
//public class GlobalExceptionHandler {
//
//    @ExceptionHandler(CompanyNotFoundException.class)
//    public ResponseEntity<ApiResponse<Void>> handleCompanyNotFound(CompanyNotFoundException ex) {
//        log.error("Company not found: {}", ex.getMessage());
//        return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                .body(ApiResponse.error(ex.getMessage()));
//    }
//
//    @ExceptionHandler(VehicleNotFoundException.class)
//    public ResponseEntity<ApiResponse<Void>> handleVehicleNotFound(VehicleNotFoundException ex) {
//        log.error("Vehicle not found: {}", ex.getMessage());
//        return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                .body(ApiResponse.error(ex.getMessage()));
//    }
//
//    @ExceptionHandler(ValidationException.class)
//    public ResponseEntity<ApiResponse<Void>> handleValidation(ValidationException ex) {
//        log.error("Validation error: {}", ex.getMessage());
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                .body(ApiResponse.error(ex.getMessage()));
//    }
//
//    @ExceptionHandler(UnauthorizedException.class)
//    public ResponseEntity<ApiResponse<Void>> handleUnauthorized(UnauthorizedException ex) {
//        log.error("Unauthorized: {}", ex.getMessage());
//        return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                .body(ApiResponse.error(ex.getMessage()));
//    }
//
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ApiResponse<Void>> handleGeneral(Exception ex) {
//        log.error("Unexpected error", ex);
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body(ApiResponse.error("An unexpected error occurred"));
//    }
//}

package com.svrmslk.company.shared.exception;

import com.svrmslk.company.shared.api.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(CompanyNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleCompanyNotFound(CompanyNotFoundException ex) {
        log.error("Company not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(VehicleNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleVehicleNotFound(VehicleNotFoundException ex) {
        log.error("Vehicle not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(ValidationException ex) {
        log.error("Validation error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnauthorized(UnauthorizedException ex) {
        log.error("Unauthorized: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * ✅ Catch IllegalArgumentException specifically for UUID parsing errors
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        log.error("Invalid argument: {}", ex.getMessage(), ex);

        // Provide user-friendly message for UUID parsing errors
        String message = ex.getMessage();
        if (message != null && message.contains("Invalid UUID")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Invalid identifier format. Please check your request."));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Invalid request: " + ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneral(Exception ex) {
        log.error("Unexpected error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("An unexpected error occurred"));
    }
}