package com.svrmslk.authservice.authentication.infrastructure.persistence;

import com.svrmslk.authservice.authentication.infrastructure.persistence.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaRefreshTokenRepository extends JpaRepository<RefreshTokenEntity, UUID> {

    Optional<RefreshTokenEntity> findByToken(String token);

    void deleteByToken(String token);

    void deleteByUserId(UUID userId);
}