// com/svrmslk/notificationservice/application/port/out/NotificationRepositoryPort.java
package com.svrmslk.notificationservice.application.port.out;

import com.svrmslk.notificationservice.domain.model.Notification;
import com.svrmslk.notificationservice.domain.model.NotificationId;

import java.util.Optional;

public interface NotificationRepositoryPort {
    Notification save(Notification notification);

    Optional<Notification> findById(NotificationId id, String tenantId);

    boolean existsByEventIdAndTenantId(String eventId, String tenantId);

    Optional<Notification> findByEventIdAndTenantId(String eventId, String tenantId);
}