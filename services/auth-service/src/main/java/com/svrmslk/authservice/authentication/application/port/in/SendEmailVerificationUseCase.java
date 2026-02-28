package com.svrmslk.authservice.authentication.application.port.in;

import java.util.UUID;

/**
 * Use case for sending email verification.
 */
public interface SendEmailVerificationUseCase {

    void sendVerification(UUID userId);
}