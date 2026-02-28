package com.svrmslk.authservice.authentication.infrastructure.persistence;

import com.svrmslk.authservice.authentication.infrastructure.persistence.entity.EmailVerificationTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaEmailVerificationTokenRepository extends JpaRepository<EmailVerificationTokenEntity, UUID> {

    Optional<EmailVerificationTokenEntity> findByTokenHash(String tokenHash);

    void deleteByUserId(UUID userId);

    void deleteByExpiresAtBefore(Instant instant);
}