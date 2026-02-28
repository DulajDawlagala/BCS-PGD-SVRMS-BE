package com.svrmslk.authservice.authentication.infrastructure.persistence;

import com.svrmslk.authservice.authentication.infrastructure.persistence.entity.ExternalIdentityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaExternalIdentityRepository extends JpaRepository<ExternalIdentityEntity, UUID> {

    Optional<ExternalIdentityEntity> findByProviderAndProviderUserId(String provider, String providerUserId);

    List<ExternalIdentityEntity> findByUserId(UUID userId);

    boolean existsByProviderAndProviderUserId(String provider, String providerUserId);
}