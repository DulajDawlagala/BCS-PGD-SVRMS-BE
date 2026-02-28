// com/svrmslk/notificationservice/infrastructure/persistence/repository/JpaNotificationRepository.java
package com.svrmslk.notificationservice.infrastructure.persistence.repository;

import com.svrmslk.notificationservice.infrastructure.persistence.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaNotificationRepository extends JpaRepository<NotificationEntity, UUID> {

    Optional<NotificationEntity> findByIdAndTenantId(UUID id, String tenantId);

    Optional<NotificationEntity> findByEventIdAndTenantId(String eventId, String tenantId);

    boolean existsByEventIdAndTenantId(String eventId, String tenantId);
}