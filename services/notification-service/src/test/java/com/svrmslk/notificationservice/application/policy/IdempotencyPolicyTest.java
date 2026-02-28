// src/test/java/com/svrmslk/notificationservice/application/policy/IdempotencyPolicyTest.java
package com.svrmslk.notificationservice.application.policy;

import com.svrmslk.notificationservice.application.port.out.NotificationRepositoryPort;
import com.svrmslk.notificationservice.domain.model.Notification;
import com.svrmslk.notificationservice.domain.model.NotificationChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IdempotencyPolicyTest {

    @Mock
    private NotificationRepositoryPort repository;

    private IdempotencyPolicy idempotencyPolicy;

    @BeforeEach
    void setUp() {
        idempotencyPolicy = new IdempotencyPolicy(repository);
    }

    @Test
    void shouldReturnUniqueForNewEvent() {
        when(repository.findByEventIdAndTenantId("event-1", "tenant-1"))
                .thenReturn(Optional.empty());

        var result = idempotencyPolicy.checkIdempotency("event-1", "tenant-1");

        assertTrue(result.isUnique());
        assertFalse(result.isDuplicate());
        assertTrue(result.getExistingNotification().isEmpty());
    }

    @Test
    void shouldReturnDuplicateForExistingEvent() {
        Notification existing = Notification.create(
                "tenant-1",
                "event-1",
                NotificationChannel.EMAIL,
                "test@example.com",
                "Subject",
                "Content",
                Map.of()
        );

        when(repository.findByEventIdAndTenantId("event-1", "tenant-1"))
                .thenReturn(Optional.of(existing));

        var result = idempotencyPolicy.checkIdempotency("event-1", "tenant-1");

        assertTrue(result.isDuplicate());
        assertFalse(result.isUnique());
        assertTrue(result.getExistingNotification().isPresent());
        assertEquals(existing, result.getExistingNotification().get());
    }
}