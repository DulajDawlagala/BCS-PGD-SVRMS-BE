package com.svrmslk.authservice.authentication.application.command;

import java.util.UUID;

public record LogoutUserCommand(
        UUID userId,
        String sessionId
) {}