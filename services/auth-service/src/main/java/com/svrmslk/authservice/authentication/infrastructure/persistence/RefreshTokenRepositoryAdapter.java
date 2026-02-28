package com.svrmslk.authservice.authentication.infrastructure.persistence;

import com.svrmslk.authservice.authentication.application.port.out.RefreshTokenRepositoryPort;
import com.svrmslk.authservice.authentication.infrastructure.persistence.entity.RefreshTokenEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Component
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepositoryPort {

    private final JpaRefreshTokenRepository jpaRepository;

    public RefreshTokenRepositoryAdapter(JpaRefreshTokenRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    @Transactional
    public void save(RefreshToken refreshToken) {
        RefreshTokenEntity entity = new RefreshTokenEntity();
        entity.setId(refreshToken.id());
        entity.setUserId(refreshToken.userId());
        entity.setToken(refreshToken.token());
        entity.setExpiresAt(refreshToken.expiresAt());
        entity.setCreatedAt(refreshToken.createdAt());
        jpaRepository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RefreshToken> findByToken(String token) {
        return jpaRepository.findByToken(token).map(this::toDomain);
    }

    @Override
    @Transactional
    public void deleteByToken(String token) {
        jpaRepository.deleteByToken(token);
    }

    @Override
    @Transactional
    public void deleteByUserId(UUID userId) {
        jpaRepository.deleteByUserId(userId);
    }

    private RefreshToken toDomain(RefreshTokenEntity entity) {
        return new RefreshToken(
                entity.getId(),
                entity.getUserId(),
                entity.getToken(),
                entity.getExpiresAt(),
                entity.getCreatedAt()
        );
    }
}