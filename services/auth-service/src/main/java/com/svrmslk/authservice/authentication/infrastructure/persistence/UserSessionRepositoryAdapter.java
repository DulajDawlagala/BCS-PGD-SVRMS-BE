package com.svrmslk.authservice.authentication.infrastructure.persistence;

import com.svrmslk.authservice.authentication.application.port.out.UserSessionRepositoryPort;
import com.svrmslk.authservice.authentication.infrastructure.persistence.entity.UserSessionEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class UserSessionRepositoryAdapter implements UserSessionRepositoryPort {

    private final JpaUserSessionRepository jpaRepository;

    public UserSessionRepositoryAdapter(JpaUserSessionRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    @Transactional
    public UserSession save(UserSession session) {
        UserSessionEntity entity = toEntity(session);
        UserSessionEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserSession> findBySessionToken(String sessionToken) {
        return jpaRepository.findBySessionToken(sessionToken).map(this::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserSession> findActiveByUserId(UUID userId) {
        return jpaRepository.findByUserIdAndActiveTrue(userId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long countActiveByUserId(UUID userId) {
        return jpaRepository.countByUserIdAndActiveTrue(userId);
    }

    @Override
    @Transactional
    public void terminateAllByUserId(UUID userId) {
        jpaRepository.deleteByUserIdAndActiveTrue(userId);
    }

    @Override
    @Transactional
    public void deleteExpiredSessions(Instant before) {
        jpaRepository.deleteByExpiresAtBefore(before);
    }

    private UserSession toDomain(UserSessionEntity entity) {
        return new UserSession(
                entity.getId(),
                entity.getUserId(),
                entity.getSessionToken(),
                entity.getDeviceInfo(),
                entity.getIpAddress(),
                entity.getUserAgent(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getLastAccessedAt(),
                entity.getExpiresAt(),
                entity.getTerminatedAt()
        );
    }

    private UserSessionEntity toEntity(UserSession domain) {
        UserSessionEntity entity = new UserSessionEntity();
        entity.setId(domain.id());
        entity.setUserId(domain.userId());
        entity.setSessionToken(domain.sessionToken());
        entity.setDeviceInfo(domain.deviceInfo());
        entity.setIpAddress(domain.ipAddress());
        entity.setUserAgent(domain.userAgent());
        entity.setActive(domain.active());
        entity.setCreatedAt(domain.createdAt());
        entity.setLastAccessedAt(domain.lastAccessedAt());
        entity.setExpiresAt(domain.expiresAt());
        entity.setTerminatedAt(domain.terminatedAt());
        return entity;
    }
}