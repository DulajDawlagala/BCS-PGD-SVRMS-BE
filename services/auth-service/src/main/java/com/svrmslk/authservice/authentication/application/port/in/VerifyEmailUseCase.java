package com.svrmslk.authservice.authentication.application.port.in;

/**
 * Use case for verifying email with token.
 */
public interface VerifyEmailUseCase {

    void verifyEmail(String token);
}