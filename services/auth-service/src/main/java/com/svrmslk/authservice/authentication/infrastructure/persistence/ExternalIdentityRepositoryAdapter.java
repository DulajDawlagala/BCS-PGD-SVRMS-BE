package com.svrmslk.authservice.authentication.infrastructure.persistence;

import com.svrmslk.authservice.authentication.application.port.out.ExternalIdentityRepositoryPort;
import com.svrmslk.authservice.authentication.infrastructure.persistence.entity.ExternalIdentityEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class ExternalIdentityRepositoryAdapter implements ExternalIdentityRepositoryPort {

    private final JpaExternalIdentityRepository jpaRepository;

    public ExternalIdentityRepositoryAdapter(JpaExternalIdentityRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    @Transactional
    public ExternalIdentity save(ExternalIdentity externalIdentity) {
        ExternalIdentityEntity entity = toEntity(externalIdentity);
        ExternalIdentityEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ExternalIdentity> findByProviderAndProviderUserId(String provider, String providerUserId) {
        return jpaRepository.findByProviderAndProviderUserId(provider, providerUserId)
                .map(this::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExternalIdentity> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByProviderAndProviderUserId(String provider, String providerUserId) {
        return jpaRepository.existsByProviderAndProviderUserId(provider, providerUserId);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    private ExternalIdentity toDomain(ExternalIdentityEntity entity) {
        return new ExternalIdentity(
                entity.getId(),
                entity.getUserId(),
                entity.getProvider(),
                entity.getProviderUserId(),
                entity.getProviderEmail(),
                entity.getProviderName(),
                entity.getAccessTokenHash(),
                entity.getRefreshTokenHash(),
                entity.getTokenExpiresAt(),
                entity.getLinkedAt(),
                entity.getLastUsedAt()
        );
    }

    private ExternalIdentityEntity toEntity(ExternalIdentity domain) {
        ExternalIdentityEntity entity = new ExternalIdentityEntity();
        entity.setId(domain.id());
        entity.setUserId(domain.userId());
        entity.setProvider(domain.provider());
        entity.setProviderUserId(domain.providerUserId());
        entity.setProviderEmail(domain.providerEmail());
        entity.setProviderName(domain.providerName());
        entity.setAccessTokenHash(domain.accessTokenHash());
        entity.setRefreshTokenHash(domain.refreshTokenHash());
        entity.setTokenExpiresAt(domain.tokenExpiresAt());
        entity.setLinkedAt(domain.linkedAt());
        entity.setLastUsedAt(domain.lastUsedAt());
        return entity;
    }
}