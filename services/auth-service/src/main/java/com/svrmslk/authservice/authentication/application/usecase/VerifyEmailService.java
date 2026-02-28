package com.svrmslk.authservice.authentication.application.usecase;

import com.svrmslk.authservice.authentication.application.port.in.VerifyEmailUseCase;
import com.svrmslk.authservice.authentication.application.port.out.EmailVerificationTokenRepositoryPort;
import com.svrmslk.authservice.authentication.application.port.out.EventPublisherPort;

import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;

/**
 * Service for verifying email addresses using verification tokens.
 */
public class VerifyEmailService implements VerifyEmailUseCase {

    private final EmailVerificationTokenRepositoryPort tokenRepository;
    private final EventPublisherPort eventPublisher;

    public VerifyEmailService(
            EmailVerificationTokenRepositoryPort tokenRepository,
            EventPublisherPort eventPublisher
    ) {
        this.tokenRepository = tokenRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void verifyEmail(String rawToken) {
        String tokenHash = hashToken(rawToken);

        var token = tokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new IllegalArgumentException("Invalid verification token"));

        if (token.used()) {
            throw new IllegalArgumentException("Token already used");
        }

        if (token.expiresAt().isBefore(Instant.now())) {
            throw new IllegalArgumentException("Token expired");
        }

        EmailVerificationTokenRepositoryPort.EmailVerificationToken updatedToken =
                new EmailVerificationTokenRepositoryPort.EmailVerificationToken(
                        token.id(),
                        token.userId(),
                        token.tokenHash(),
                        token.expiresAt(),
                        token.createdAt(),
                        Instant.now(),
                        true
                );

        tokenRepository.save(updatedToken);

        eventPublisher.publish(new EmailVerifiedEvent(token.userId().toString()));
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to hash token", e);
        }
    }

    private record EmailVerifiedEvent(String userId) implements EventPublisherPort.DomainEvent {
        @Override
        public String getEventType() {
            return "EmailVerified";
        }

        @Override
        public String getAggregateId() {
            return userId;
        }
    }
}