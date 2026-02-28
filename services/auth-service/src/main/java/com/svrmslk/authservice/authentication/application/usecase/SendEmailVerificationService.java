
package com.svrmslk.authservice.authentication.application.usecase;

import com.svrmslk.authservice.authentication.application.port.in.SendEmailVerificationUseCase;
import com.svrmslk.authservice.authentication.application.port.out.EmailSenderPort;
import com.svrmslk.authservice.authentication.application.port.out.EmailVerificationTokenRepositoryPort;
import com.svrmslk.authservice.authentication.domain.model.UserAccount;
import com.svrmslk.authservice.authentication.domain.repository.UserAccountRepository;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.UUID;

/**
 * Application service for sending email verification tokens.
 */
public class SendEmailVerificationService implements SendEmailVerificationUseCase {

    private static final long TOKEN_VALIDITY_HOURS = 24;
    private static final int TOKEN_LENGTH_BYTES = 56;

    private final UserAccountRepository userAccountRepository;
    private final EmailVerificationTokenRepositoryPort tokenRepository;
    private final EmailSenderPort emailSender;
    private final SecureRandom secureRandom = new SecureRandom();

    public SendEmailVerificationService(
            UserAccountRepository userAccountRepository,
            EmailVerificationTokenRepositoryPort tokenRepository,
            EmailSenderPort emailSender
    ) {
        this.userAccountRepository = userAccountRepository;
        this.tokenRepository = tokenRepository;
        this.emailSender = emailSender;
    }

    @Override
    public void sendVerification(UUID userId) {
        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        // Remove existing tokens
        tokenRepository.deleteByUserId(userId);

        // Generate token
        String rawToken = generateSecureToken();
        String tokenHash = hashToken(rawToken);

        Instant now = Instant.now();
        Instant expiresAt = now.plus(TOKEN_VALIDITY_HOURS, ChronoUnit.HOURS);

        EmailVerificationTokenRepositoryPort.EmailVerificationToken token =
                new EmailVerificationTokenRepositoryPort.EmailVerificationToken(
                        UUID.randomUUID(),
                        userId,
                        tokenHash,
                        expiresAt,
                        now,
                        null,
                        false
                );

        tokenRepository.save(token);

        // Send email (delegated to infrastructure)
        emailSender.sendVerificationEmail(
                user.getEmail().getValue(),
                rawToken
        );
    }

    private String generateSecureToken() {
        byte[] randomBytes = new byte[TOKEN_LENGTH_BYTES];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-384");
            byte[] hash = digest.digest(token.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to hash verification token", e);
        }
    }
}

