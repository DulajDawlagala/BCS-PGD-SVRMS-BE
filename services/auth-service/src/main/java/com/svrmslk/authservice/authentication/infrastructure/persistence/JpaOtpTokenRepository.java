package com.svrmslk.authservice.authentication.infrastructure.persistence;

import com.svrmslk.authservice.authentication.infrastructure.persistence.entity.OtpTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaOtpTokenRepository extends JpaRepository<OtpTokenEntity, UUID> {

    Optional<OtpTokenEntity> findByUserIdAndOtpTypeAndUsedFalse(UUID userId, String otpType);

    void deleteByUserId(UUID userId);

    void deleteByExpiresAtBefore(Instant instant);
}