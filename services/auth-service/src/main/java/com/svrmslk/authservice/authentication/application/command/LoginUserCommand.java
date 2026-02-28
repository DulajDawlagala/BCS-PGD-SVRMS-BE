package com.svrmslk.authservice.authentication.application.command;

public record LoginUserCommand(
        String email,
        String password,
        String ipAddress,
        String userAgent
) {}