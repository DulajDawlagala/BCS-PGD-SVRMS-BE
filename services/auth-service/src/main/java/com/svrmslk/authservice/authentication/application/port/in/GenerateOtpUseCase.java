package com.svrmslk.authservice.authentication.application.port.in;

import java.util.UUID;

/**
 * Use case for generating OTP.
 */
public interface GenerateOtpUseCase {

    void generateOtp(UUID userId, String otpType);
}