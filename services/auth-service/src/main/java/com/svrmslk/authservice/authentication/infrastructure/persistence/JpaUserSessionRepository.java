package com.svrmslk.authservice.authentication.infrastructure.persistence;

import com.svrmslk.authservice.authentication.infrastructure.persistence.entity.UserSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaUserSessionRepository extends JpaRepository<UserSessionEntity, UUID> {

    Optional<UserSessionEntity> findBySessionToken(String sessionToken);

    List<UserSessionEntity> findByUserIdAndActiveTrue(UUID userId);

    long countByUserIdAndActiveTrue(UUID userId);

    void deleteByUserIdAndActiveTrue(UUID userId);

    void deleteByExpiresAtBefore(Instant instant);
}