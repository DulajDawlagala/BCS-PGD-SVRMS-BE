package com.svrmslk.authservice.authentication.infrastructure.persistence;

import com.svrmslk.authservice.authentication.application.port.out.OtpTokenRepositoryPort;
import com.svrmslk.authservice.authentication.infrastructure.persistence.entity.OtpTokenEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
public class OtpTokenRepositoryAdapter implements OtpTokenRepositoryPort {

    private final JpaOtpTokenRepository jpaRepository;

    public OtpTokenRepositoryAdapter(JpaOtpTokenRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    @Transactional
    public OtpToken save(OtpToken token) {
        OtpTokenEntity entity = toEntity(token);
        OtpTokenEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OtpToken> findActiveByUserIdAndType(UUID userId, String otpType) {
        return jpaRepository.findByUserIdAndOtpTypeAndUsedFalse(userId, otpType)
                .map(this::toDomain);
    }

    @Override
    @Transactional
    public void deleteByUserId(UUID userId) {
        jpaRepository.deleteByUserId(userId);
    }

    @Override
    @Transactional
    public void deleteExpiredTokens(Instant before) {
        jpaRepository.deleteByExpiresAtBefore(before);
    }

    private OtpToken toDomain(OtpTokenEntity entity) {
        return new OtpToken(
                entity.getId(),
                entity.getUserId(),
                entity.getOtpHash(),
                entity.getOtpType(),
                entity.getExpiresAt(),
                entity.getCreatedAt(),
                entity.getVerifiedAt(),
                entity.getFailedAttempts(),
                entity.isUsed()
        );
    }

    private OtpTokenEntity toEntity(OtpToken domain) {
        OtpTokenEntity entity = new OtpTokenEntity();
        entity.setId(domain.id());
        entity.setUserId(domain.userId());
        entity.setOtpHash(domain.otpHash());
        entity.setOtpType(domain.otpType());
        entity.setExpiresAt(domain.expiresAt());
        entity.setCreatedAt(domain.createdAt());
        entity.setVerifiedAt(domain.verifiedAt());
        entity.setFailedAttempts(domain.failedAttempts());
        entity.setUsed(domain.used());
        return entity;
    }
}