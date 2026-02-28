package com.svrmslk.authservice.authentication.application.port.out;

/**
 * Port for sending emails (verification, OTP, etc.).
 */
public interface EmailSenderPort {

    void sendVerificationEmail(String toEmail, String verificationToken);

    void sendOtpEmail(String toEmail, String otp);

    void sendPasswordResetEmail(String toEmail, String resetToken);
}