package com.svrmslk.authservice.authentication.application.usecase;

import com.svrmslk.authservice.authentication.application.port.in.VerifyOtpUseCase;
import com.svrmslk.authservice.authentication.application.port.out.EventPublisherPort;
import com.svrmslk.authservice.authentication.application.port.out.OtpTokenRepositoryPort;

import java.security.MessageDigest;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

/**
 * Service for verifying OTP tokens using constant-time comparison.
 */
public class VerifyOtpService implements VerifyOtpUseCase {

    private static final int MAX_FAILED_ATTEMPTS = 3;

    private final OtpTokenRepositoryPort otpTokenRepository;
    private final EventPublisherPort eventPublisher;

    public VerifyOtpService(
            OtpTokenRepositoryPort otpTokenRepository,
            EventPublisherPort eventPublisher
    ) {
        this.otpTokenRepository = otpTokenRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void verifyOtp(UUID userId, String rawOtp, String otpType) {
        var token = otpTokenRepository.findActiveByUserIdAndType(userId, otpType)
                .orElseThrow(() -> new IllegalArgumentException("No active OTP found"));

        if (token.used()) {
            throw new IllegalArgumentException("OTP already used");
        }

        if (token.expiresAt().isBefore(Instant.now())) {
            throw new IllegalArgumentException("OTP expired");
        }

        if (token.failedAttempts() >= MAX_FAILED_ATTEMPTS) {
            throw new IllegalArgumentException("Too many failed attempts");
        }

        String otpHash = hashOtp(rawOtp);

        if (!constantTimeEquals(otpHash, token.otpHash())) {
            OtpTokenRepositoryPort.OtpToken updated = new OtpTokenRepositoryPort.OtpToken(
                    token.id(),
                    token.userId(),
                    token.otpHash(),
                    token.otpType(),
                    token.expiresAt(),
                    token.createdAt(),
                    null,
                    token.failedAttempts() + 1,
                    false
            );
            otpTokenRepository.save(updated);
            throw new IllegalArgumentException("Invalid OTP");
        }

        OtpTokenRepositoryPort.OtpToken verified = new OtpTokenRepositoryPort.OtpToken(
                token.id(),
                token.userId(),
                token.otpHash(),
                token.otpType(),
                token.expiresAt(),
                token.createdAt(),
                Instant.now(),
                token.failedAttempts(),
                true
        );

        otpTokenRepository.save(verified);

        eventPublisher.publish(new OtpVerifiedEvent(userId.toString(), otpType));
    }

    private String hashOtp(String otp) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(otp.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to hash OTP", e);
        }
    }

    private boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null) {
            return false;
        }
        return MessageDigest.isEqual(
                a.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                b.getBytes(java.nio.charset.StandardCharsets.UTF_8)
        );
    }

    private record OtpVerifiedEvent(String userId, String otpType) implements EventPublisherPort.DomainEvent {
        @Override
        public String getEventType() {
            return "OtpVerified";
        }

        @Override
        public String getAggregateId() {
            return userId;
        }
    }
}