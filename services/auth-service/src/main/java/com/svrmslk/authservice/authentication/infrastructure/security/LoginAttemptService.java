package com.svrmslk.authservice.authentication.infrastructure.security;

import com.svrmslk.authservice.authentication.infrastructure.persistence.JpaLoginAttemptRepository;
import com.svrmslk.authservice.authentication.infrastructure.persistence.entity.LoginAttemptEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * Service for tracking and analyzing login attempts.
 *
 * Used for security monitoring, auditing, and rate limiting.
 */
@Service
public class LoginAttemptService {

    private static final Logger logger =
            LoggerFactory.getLogger(LoginAttemptService.class);

    /** Time window for tracking attempts (minutes) */
    private static final long TRACKING_WINDOW_MINUTES = 15;

    /** Maximum failed attempts allowed per IP in the tracking window */
    private static final int MAX_ATTEMPTS_PER_IP = 10;

    private final JpaLoginAttemptRepository repository;

    public LoginAttemptService(JpaLoginAttemptRepository repository) {
        this.repository = repository;
    }

    /**
     * Records a successful login attempt.
     */
    @Transactional
    public void recordSuccessfulLogin(
            UUID userId,
            String email,
            String ipAddress,
            String userAgent
    ) {
        LoginAttemptEntity attempt = createAttempt(
                userId,
                email,
                ipAddress,
                userAgent,
                true,
                null
        );

        repository.save(attempt);
        logger.info("Successful login recorded for user: {}", email);
    }

    /**
     * Records a failed login attempt.
     */
    @Transactional
    public void recordFailedLogin(
            String email,
            String ipAddress,
            String userAgent,
            String reason
    ) {
        LoginAttemptEntity attempt = createAttempt(
                null,
                email,
                ipAddress,
                userAgent,
                false,
                reason
        );

        repository.save(attempt);
        logger.warn(
                "Failed login attempt for email: {} from IP: {}",
                email,
                ipAddress
        );
    }

    /**
     * Determines whether an IP address should be temporarily blocked
     * due to excessive failed login attempts.
     */
    @Transactional(readOnly = true)
    public boolean isIpBlocked(String ipAddress) {
        Instant threshold =
                Instant.now().minus(TRACKING_WINDOW_MINUTES, ChronoUnit.MINUTES);

        long failedAttempts = repository
                .findByIpAddressAndAttemptTimestampAfter(ipAddress, threshold)
                .stream()
                .filter(attempt -> !attempt.isSuccessful())
                .count();

        return failedAttempts >= MAX_ATTEMPTS_PER_IP;
    }

    /**
     * Factory method for creating LoginAttemptEntity instances.
     */
    private LoginAttemptEntity createAttempt(
            UUID userId,
            String email,
            String ipAddress,
            String userAgent,
            boolean successful,
            String failureReason
    ) {
        LoginAttemptEntity entity = new LoginAttemptEntity();
        entity.setId(UUID.randomUUID());
        entity.setUserId(userId);
        entity.setEmail(email);
        entity.setIpAddress(ipAddress);
        entity.setUserAgent(userAgent);
        entity.setSuccessful(successful);
        entity.setFailureReason(failureReason);
        entity.setAttemptTimestamp(Instant.now());
        return entity;
    }
}
