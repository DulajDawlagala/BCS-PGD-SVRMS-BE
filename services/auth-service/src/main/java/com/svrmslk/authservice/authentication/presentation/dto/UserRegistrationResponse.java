package com.svrmslk.authservice.authentication.presentation.dto;

import java.util.UUID;

public record UserRegistrationResponse(
        UUID userId,
        String email
) {}