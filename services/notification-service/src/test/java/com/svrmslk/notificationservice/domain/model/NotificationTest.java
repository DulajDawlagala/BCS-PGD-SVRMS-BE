// src/test/java/com/svrmslk/notificationservice/domain/model/NotificationTest.java
package com.svrmslk.notificationservice.domain.model;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class NotificationTest {

    @Test
    void shouldCreateEmailNotification() {
        Notification notification = Notification.create(
                "tenant-1",
                "event-1",
                NotificationChannel.EMAIL,
                "test@example.com",
                "Test Subject",
                "Test Content",
                Map.of("key", "value")
        );

        assertNotNull(notification);
        assertEquals("tenant-1", notification.tenantId());
        assertEquals("event-1", notification.eventId());
        assertEquals(NotificationChannel.EMAIL, notification.channel());
        assertEquals("test@example.com", notification.recipient());
        assertEquals("Test Subject", notification.subject());
        assertEquals("Test Content", notification.content());
        assertEquals(NotificationStatus.PENDING, notification.status());
        assertNotNull(notification.id());
    }

    @Test
    void shouldCreateInAppNotification() {
        Notification notification = Notification.create(
                "tenant-1",
                "event-1",
                NotificationChannel.IN_APP,
                "user-123",
                null,
                "Test Content",
                Map.of()
        );

        assertNotNull(notification);
        assertEquals(NotificationChannel.IN_APP, notification.channel());
        assertNull(notification.subject());
    }

    @Test
    void shouldThrowExceptionForNullTenantId() {
        assertThrows(IllegalArgumentException.class, () ->
                Notification.create(
                        null,
                        "event-1",
                        NotificationChannel.EMAIL,
                        "test@example.com",
                        "Subject",
                        "Content",
                        Map.of()
                )
        );
    }

    @Test
    void shouldThrowExceptionForInvalidEmail() {
        assertThrows(IllegalArgumentException.class, () ->
                Notification.create(
                        "tenant-1",
                        "event-1",
                        NotificationChannel.EMAIL,
                        "invalid-email",
                        "Subject",
                        "Content",
                        Map.of()
                )
        );
    }

    @Test
    void shouldMarkNotificationAsSent() {
        Notification notification = Notification.create(
                "tenant-1",
                "event-1",
                NotificationChannel.EMAIL,
                "test@example.com",
                "Subject",
                "Content",
                Map.of()
        );

        notification.markAsSent();

        assertEquals(NotificationStatus.SENT, notification.status());
        assertNull(notification.failureReason());
    }

    @Test
    void shouldMarkNotificationAsFailed() {
        Notification notification = Notification.create(
                "tenant-1",
                "event-1",
                NotificationChannel.EMAIL,
                "test@example.com",
                "Subject",
                "Content",
                Map.of()
        );

        notification.markAsFailed("SendGrid error");

        assertEquals(NotificationStatus.FAILED, notification.status());
        assertEquals("SendGrid error", notification.failureReason());
    }

    @Test
    void shouldNotAllowTransitionFromSentToFailed() {
        Notification notification = Notification.create(
                "tenant-1",
                "event-1",
                NotificationChannel.EMAIL,
                "test@example.com",
                "Subject",
                "Content",
                Map.of()
        );

        notification.markAsSent();

        assertThrows(IllegalStateException.class, () ->
                notification.markAsFailed("Error")
        );
    }
}