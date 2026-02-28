package com.svrmslk.common.events.domain.notification;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for InAppNotificationRequestedEvent.
 */
class InAppNotificationRequestedEventTest {

    @Test
    void shouldCreateEventWithBuilder() {
        // When
        InAppNotificationRequestedEvent event = InAppNotificationRequestedEvent.builder()
                .tenantId("tenant-1")
                .userId("user-123")
                .title("New Message")
                .message("You have received a new message")
                .notificationType(InAppNotificationRequestedEvent.NotificationType.INFO)
                .category("messaging")
                .priority(InAppNotificationRequestedEvent.NotificationPriority.NORMAL)
                .actionUrl("/messages/inbox")
                .actionLabel("View Message")
                .requestedBy("system")
                .build();

        // Then
        assertThat(event).isNotNull();
        assertThat(event.tenantId()).isEqualTo("tenant-1");
        assertThat(event.userId()).isEqualTo("user-123");
        assertThat(event.title()).isEqualTo("New Message");
        assertThat(event.notificationType()).isEqualTo(InAppNotificationRequestedEvent.NotificationType.INFO);
    }

    @Test
    void shouldValidateRequiredFields() {
        // When/Then
        assertThatThrownBy(() ->
                InAppNotificationRequestedEvent.builder()
                        .tenantId("tenant-1")
                        .build()
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldSupportCustomData() {
        // When
        InAppNotificationRequestedEvent event = InAppNotificationRequestedEvent.builder()
                .tenantId("tenant-1")
                .userId("user-123")
                .title("Alert")
                .message("System alert")
                .data(Map.of("alertId", "alert-456", "severity", "high"))
                .requestedBy("system")
                .build();

        // Then
        assertThat(event.data()).containsEntry("alertId", "alert-456");
        assertThat(event.data()).containsEntry("severity", "high");
    }

    @Test
    void shouldSupportExpiration() {
        // Given
        Instant expiresAt = Instant.now().plusSeconds(3600);

        // When
        InAppNotificationRequestedEvent event = InAppNotificationRequestedEvent.builder()
                .tenantId("tenant-1")
                .userId("user-123")
                .title("Temporary Alert")
                .message("This notification expires in 1 hour")
                .expiresAt(expiresAt)
                .requestedBy("system")
                .build();

        // Then
        assertThat(event.expiresAt()).isEqualTo(expiresAt);
    }

    @Test
    void shouldSupportAcknowledgmentRequirement() {
        // When
        InAppNotificationRequestedEvent event = InAppNotificationRequestedEvent.builder()
                .tenantId("tenant-1")
                .userId("user-123")
                .title("Important Notice")
                .message("Please acknowledge this message")
                .requiresAcknowledgment(true)
                .requestedBy("system")
                .build();

        // Then
        assertThat(event.requiresAcknowledgment()).isTrue();
    }
}