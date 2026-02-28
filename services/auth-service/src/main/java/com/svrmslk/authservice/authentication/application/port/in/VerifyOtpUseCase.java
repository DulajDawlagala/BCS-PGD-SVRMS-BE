package com.svrmslk.authservice.authentication.application.port.in;

import java.util.UUID;

/**
 * Use case for verifying OTP.
 */
public interface VerifyOtpUseCase {

    void verifyOtp(UUID userId, String otp, String otpType);
}