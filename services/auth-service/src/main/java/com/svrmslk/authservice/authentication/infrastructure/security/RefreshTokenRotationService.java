package com.svrmslk.authservice.authentication.infrastructure.security;

import com.svrmslk.authservice.authentication.application.port.out.EventPublisherPort;
import com.svrmslk.authservice.authentication.application.port.out.RefreshTokenRepositoryPort;
import com.svrmslk.authservice.authentication.application.port.out.TokenGeneratorPort;
import com.svrmslk.authservice.authentication.domain.model.UserAccount;
import com.svrmslk.authservice.authentication.domain.policy.RefreshTokenReusePolicy;
import com.svrmslk.authservice.authentication.domain.repository.UserAccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * Service for handling refresh token rotation with reuse detection.
 * Implements security best practices for token refresh.
 */
@Service
public class RefreshTokenRotationService {

    private static final Logger logger = LoggerFactory.getLogger(RefreshTokenRotationService.class);
    private static final long REFRESH_TOKEN_VALIDITY_DAYS = 30;

    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final UserAccountRepository userAccountRepository;
    private final TokenGeneratorPort tokenGenerator;
    private final RefreshTokenReusePolicy reusePolicy;
    private final EventPublisherPort eventPublisher;

    public RefreshTokenRotationService(
            RefreshTokenRepositoryPort refreshTokenRepository,
            UserAccountRepository userAccountRepository,
            TokenGeneratorPort tokenGenerator,
            RefreshTokenReusePolicy reusePolicy,
            EventPublisherPort eventPublisher
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userAccountRepository = userAccountRepository;
        this.tokenGenerator = tokenGenerator;
        this.reusePolicy = reusePolicy;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public RotationResult rotateToken(String oldRefreshToken) {
        var existingToken = refreshTokenRepository.findByToken(oldRefreshToken);

        if (existingToken.isEmpty()) {
            logger.warn("Refresh token not found - possible reuse attempt");
            return new RotationResult(null, null, true);
        }

        var token = existingToken.get();

        if (token.expiresAt().isBefore(Instant.now())) {
            refreshTokenRepository.deleteByToken(oldRefreshToken);
            throw new IllegalArgumentException("Refresh token expired");
        }

        UserAccount userAccount = userAccountRepository.findById(token.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        refreshTokenRepository.deleteByToken(oldRefreshToken);

        String newAccessToken = tokenGenerator.generateAccessToken(
                userAccount,
                TokenGeneratorPort.TenantContext.empty()
        );

        String newRefreshToken = tokenGenerator.generateRefreshToken(userAccount);

        refreshTokenRepository.save(new RefreshTokenRepositoryPort.RefreshToken(
                UUID.randomUUID(),
                userAccount.getId(),
                newRefreshToken,
                Instant.now().plus(REFRESH_TOKEN_VALIDITY_DAYS, ChronoUnit.DAYS),
                Instant.now()
        ));

        eventPublisher.publish(new TokenRotatedEvent(userAccount.getId().toString()));

        return new RotationResult(newAccessToken, newRefreshToken, false);
    }

    @Transactional
    public void handleTokenReuse(UUID userId) {
        logger.warn("Token reuse detected for user: {}", userId);

        refreshTokenRepository.deleteByUserId(userId);

        userAccountRepository.findById(userId).ifPresent(user -> {
            user.invalidateSession();
            userAccountRepository.save(user);
        });

        eventPublisher.publish(new TokenReuseDetectedEvent(userId.toString()));
    }

    public record RotationResult(String accessToken, String refreshToken, boolean reuseDetected) {}

    private record TokenRotatedEvent(String userId) implements EventPublisherPort.DomainEvent {
        @Override
        public String getEventType() {
            return "RefreshTokenRotated";
        }

        @Override
        public String getAggregateId() {
            return userId;
        }
    }

    private record TokenReuseDetectedEvent(String userId) implements EventPublisherPort.DomainEvent {
        @Override
        public String getEventType() {
            return "RefreshTokenReuseDetected";
        }

        @Override
        public String getAggregateId() {
            return userId;
        }
    }
}