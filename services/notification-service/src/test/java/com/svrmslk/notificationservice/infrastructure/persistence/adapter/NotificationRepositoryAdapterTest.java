// src/test/java/com/svrmslk/notificationservice/infrastructure/persistence/adapter/NotificationRepositoryAdapterTest.java
package com.svrmslk.notificationservice.infrastructure.persistence.adapter;

import com.svrmslk.notificationservice.common.context.ContextHolder;
import com.svrmslk.notificationservice.common.context.TenantContext;
import com.svrmslk.notificationservice.domain.model.Notification;
import com.svrmslk.notificationservice.domain.model.NotificationChannel;
import com.svrmslk.notificationservice.infrastructure.persistence.entity.NotificationEntity;
import com.svrmslk.notificationservice.infrastructure.persistence.repository.JpaNotificationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationRepositoryAdapterTest {

    @Mock
    private JpaNotificationRepository jpaRepository;

    private NotificationRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new NotificationRepositoryAdapter(jpaRepository);
        ContextHolder.setTenantContext(new TenantContext("tenant-1"));
    }

    @AfterEach
    void tearDown() {
        ContextHolder.clearAll();
    }

    @Test
    void shouldSaveNotification() {
        Notification notification = Notification.create(
                "tenant-1",
                "event-1",
                NotificationChannel.EMAIL,
                "test@example.com",
                "Subject",
                "Content",
                Map.of()
        );

        NotificationEntity savedEntity = new NotificationEntity();
        savedEntity.setId(notification.id().value());
        savedEntity.setTenantId(notification.tenantId());
        savedEntity.setEventId(notification.eventId());

        when(jpaRepository.save(any(NotificationEntity.class))).thenReturn(savedEntity);

        Notification saved = adapter.save(notification);

        assertNotNull(saved);
        assertEquals(notification.tenantId(), saved.tenantId());
    }

    @Test
    void shouldCheckExistenceByEventIdAndTenantId() {
        when(jpaRepository.existsByEventIdAndTenantId("event-1", "tenant-1"))
                .thenReturn(true);

        boolean exists = adapter.existsByEventIdAndTenantId("event-1", "tenant-1");

        assertTrue(exists);
    }
}