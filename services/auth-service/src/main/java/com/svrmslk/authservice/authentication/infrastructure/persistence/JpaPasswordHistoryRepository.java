package com.svrmslk.authservice.authentication.infrastructure.persistence;

import com.svrmslk.authservice.authentication.infrastructure.persistence.entity.PasswordHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaPasswordHistoryRepository extends JpaRepository<PasswordHistoryEntity, UUID> {

    @Query("SELECT p FROM PasswordHistoryEntity p WHERE p.userId = :userId ORDER BY p.createdAt DESC")
    List<PasswordHistoryEntity> findRecentByUserIdOrderByCreatedAtDesc(UUID userId);

    void deleteByUserId(UUID userId);
}