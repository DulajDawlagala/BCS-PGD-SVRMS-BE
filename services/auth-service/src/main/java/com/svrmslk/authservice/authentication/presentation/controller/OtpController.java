package com.svrmslk.authservice.authentication.presentation.controller;

import com.svrmslk.authservice.authentication.application.port.in.GenerateOtpUseCase;
import com.svrmslk.authservice.authentication.application.port.in.VerifyOtpUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for OTP (One-Time Password) operations.
 * Handles OTP generation and verification for multi-factor authentication.
 */
@RestController
@RequestMapping("/api/v1/auth/otp")
@Tag(name = "OTP", description = "One-Time Password endpoints for multi-factor authentication")
public class OtpController {

    private final GenerateOtpUseCase generateOtpUseCase;
    private final VerifyOtpUseCase verifyOtpUseCase;

    public OtpController(
            GenerateOtpUseCase generateOtpUseCase,
            VerifyOtpUseCase verifyOtpUseCase
    ) {
        this.generateOtpUseCase = generateOtpUseCase;
        this.verifyOtpUseCase = verifyOtpUseCase;
    }

    @PostMapping("/generate")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(
            summary = "Generate OTP",
            description = "Generates and sends a 6-digit OTP to the authenticated user's email."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OTP generated and sent successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "429", description = "Too many OTP requests")
    })
    public ResponseEntity<Void> generateOtp(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "LOGIN") String type
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        generateOtpUseCase.generateOtp(userId, type);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(
            summary = "Verify OTP",
            description = "Verifies the OTP code provided by the user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OTP verified successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid or expired OTP"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<Void> verifyOtp(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam @NotBlank @Pattern(regexp = "\\d{6}") String otp,
            @RequestParam(defaultValue = "LOGIN") String type
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        verifyOtpUseCase.verifyOtp(userId, otp, type);
        return ResponseEntity.ok().build();
    }
}