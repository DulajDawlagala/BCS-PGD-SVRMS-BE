package com.svrmslk.authservice.authentication.application.command;

public record RegisterUserCommand(
        String email,
        String password,
        String initialRole
) {}