package com.svrmslk.authservice.authentication.infrastructure.persistence;

import com.svrmslk.authservice.authentication.infrastructure.persistence.entity.LoginAttemptEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface JpaLoginAttemptRepository extends JpaRepository<LoginAttemptEntity, UUID> {

    List<LoginAttemptEntity> findByUserIdAndAttemptTimestampAfter(UUID userId, Instant after);

    List<LoginAttemptEntity> findByIpAddressAndAttemptTimestampAfter(String ipAddress, Instant after);

    long countByEmailAndSuccessfulFalseAndAttemptTimestampAfter(String email, Instant after);

    void deleteByAttemptTimestampBefore(Instant before);
}