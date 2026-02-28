package com.svrmslk.authservice.authentication.infrastructure.persistence;

import com.svrmslk.authservice.authentication.infrastructure.persistence.entity.UserAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaUserAccountRepository extends JpaRepository<UserAccountEntity, UUID> {

    Optional<UserAccountEntity> findByEmail(String email);

    boolean existsByEmail(String email);
    Optional<UserAccountEntity> findBySessionId(String sessionId);

}