package com.svrmslk.authservice.authentication.infrastructure.security;

import com.svrmslk.authservice.authentication.application.port.out.UserSessionRepositoryPort;
import com.svrmslk.authservice.authentication.domain.policy.SessionLimitPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

/**
 * Service for managing user sessions with concurrent session limits.
 */
@Service
public class SessionManagementService {

    private static final Logger logger = LoggerFactory.getLogger(SessionManagementService.class);
    private static final long SESSION_VALIDITY_HOURS = 24;

    private final UserSessionRepositoryPort sessionRepository;
    private final SessionLimitPolicy sessionLimitPolicy;

    public SessionManagementService(
            UserSessionRepositoryPort sessionRepository,
            SessionLimitPolicy sessionLimitPolicy
    ) {
        this.sessionRepository = sessionRepository;
        this.sessionLimitPolicy = sessionLimitPolicy;
    }

    @Transactional
    public String createSession(
            UUID userId,
            String deviceInfo,
            String ipAddress,
            String userAgent
    ) {
        long activeSessions = sessionRepository.countActiveByUserId(userId);

        if (sessionLimitPolicy.exceedsSessionLimit(activeSessions)) {
            terminateOldestSession(userId);
        }

        String sessionToken = UUID.randomUUID().toString();

        UserSessionRepositoryPort.UserSession session =
                new UserSessionRepositoryPort.UserSession(
                        UUID.randomUUID(),
                        userId,
                        sessionToken,
                        deviceInfo,
                        ipAddress,
                        userAgent,
                        true,
                        Instant.now(),
                        Instant.now(),
                        Instant.now().plus(SESSION_VALIDITY_HOURS, ChronoUnit.HOURS),
                        null
                );

        sessionRepository.save(session);
        logger.info("Session created for user: {}", userId);

        return sessionToken;
    }

    @Transactional
    public void terminateSession(String sessionToken) {
        sessionRepository.findBySessionToken(sessionToken).ifPresent(session -> {
            UserSessionRepositoryPort.UserSession terminated =
                    new UserSessionRepositoryPort.UserSession(
                            session.id(),
                            session.userId(),
                            session.sessionToken(),
                            session.deviceInfo(),
                            session.ipAddress(),
                            session.userAgent(),
                            false,
                            session.createdAt(),
                            session.lastAccessedAt(),
                            session.expiresAt(),
                            Instant.now()
                    );
            sessionRepository.save(terminated);
            logger.info("Session terminated: {}", sessionToken);
        });
    }

    @Transactional
    public void terminateAllSessions(UUID userId) {
        sessionRepository.terminateAllByUserId(userId);
        logger.info("All sessions terminated for user: {}", userId);
    }

    @Transactional(readOnly = true)
    public List<UserSessionRepositoryPort.UserSession> getActiveSessions(UUID userId) {
        return sessionRepository.findActiveByUserId(userId);
    }

    @Transactional
    public void cleanupExpiredSessions() {
        sessionRepository.deleteExpiredSessions(Instant.now());
    }

    private void terminateOldestSession(UUID userId) {
        List<UserSessionRepositoryPort.UserSession> sessions = sessionRepository.findActiveByUserId(userId);
        sessions.stream()
                .min((s1, s2) -> s1.createdAt().compareTo(s2.createdAt()))
                .ifPresent(oldest -> terminateSession(oldest.sessionToken()));
    }
}