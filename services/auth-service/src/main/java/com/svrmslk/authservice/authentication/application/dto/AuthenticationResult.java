package com.svrmslk.authservice.authentication.application.dto;

public record AuthenticationResult(
        String accessToken,
        String refreshToken,
        long expiresIn
) {}