package com.svrmslk.authservice.authentication.infrastructure.persistence;

import com.svrmslk.authservice.authentication.application.port.out.EmailVerificationTokenRepositoryPort;
import com.svrmslk.authservice.authentication.infrastructure.persistence.entity.EmailVerificationTokenEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
public class EmailVerificationTokenRepositoryAdapter implements EmailVerificationTokenRepositoryPort {

    private final JpaEmailVerificationTokenRepository jpaRepository;

    public EmailVerificationTokenRepositoryAdapter(JpaEmailVerificationTokenRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    @Transactional
    public EmailVerificationToken save(EmailVerificationToken token) {
        EmailVerificationTokenEntity entity = toEntity(token);
        EmailVerificationTokenEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EmailVerificationToken> findByTokenHash(String tokenHash) {
        return jpaRepository.findByTokenHash(tokenHash).map(this::toDomain);
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

    private EmailVerificationToken toDomain(EmailVerificationTokenEntity entity) {
        return new EmailVerificationToken(
                entity.getId(),
                entity.getUserId(),
                entity.getTokenHash(),
                entity.getExpiresAt(),
                entity.getCreatedAt(),
                entity.getVerifiedAt(),
                entity.isUsed()
        );
    }

    private EmailVerificationTokenEntity toEntity(EmailVerificationToken domain) {
        EmailVerificationTokenEntity entity = new EmailVerificationTokenEntity();
        entity.setId(domain.id());
        entity.setUserId(domain.userId());
        entity.setTokenHash(domain.tokenHash());
        entity.setExpiresAt(domain.expiresAt());
        entity.setCreatedAt(domain.createdAt());
        entity.setVerifiedAt(domain.verifiedAt());
        entity.setUsed(domain.used());
        return entity;
    }
}