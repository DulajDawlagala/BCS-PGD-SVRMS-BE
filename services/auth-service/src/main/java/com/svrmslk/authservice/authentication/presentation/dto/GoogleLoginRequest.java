package com.svrmslk.authservice.authentication.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO for Google OAuth login.
 *
 * SECURITY MODEL:
 * - Client sends ONLY the Google ID token
 * - Backend verifies token and extracts all identity claims
 * - No client-supplied identity data is trusted
 *
 * Flow:
 * 1. Frontend authenticates user using Google Sign-In
 * 2. Frontend receives Google ID token
 * 3. Frontend sends ID token to backend
 * 4. Backend verifies token (signature, issuer, audience, expiry)
 * 5. Backend extracts user identity and issues JWT
 */
@Schema(description = "Google OAuth login request containing Google ID token")
public record GoogleLoginRequest(

        @NotBlank(message = "Google ID token is required")
        @Schema(
                description = "Google ID token obtained from Google Sign-In",
                example = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjEifQ..."
        )
        String idToken
) {}
