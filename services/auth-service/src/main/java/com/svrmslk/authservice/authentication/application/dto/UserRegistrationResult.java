package com.svrmslk.authservice.authentication.application.dto;

import java.util.UUID;

public record UserRegistrationResult(
        UUID userId,
        String email
) {}