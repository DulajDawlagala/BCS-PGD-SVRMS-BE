package com.svrmslk.authservice.authentication.application.command;

import java.util.UUID;

public record ChangePasswordCommand(
        UUID userId,
        String currentPassword,
        String newPassword
) {}