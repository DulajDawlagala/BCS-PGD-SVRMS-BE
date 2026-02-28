package com.svrmslk.authservice.authentication.application.command;

public record RefreshTokenCommand(
        String refreshToken
) {}