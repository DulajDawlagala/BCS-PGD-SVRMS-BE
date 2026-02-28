package com.svrmslk.common.events.domain.notification;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for EmailNotificationRequestedEvent.
 */
class EmailNotificationRequestedEventTest {

    @Test
    void shouldCreateEventWithBuilder() {
        // When
        EmailNotificationRequestedEvent event = EmailNotificationRequestedEvent.builder()
                .tenantId("tenant-1")
                .recipientEmail("test@example.com")
                .recipientName("Test User")
                .subject("Welcome Email")
                .templateId("welcome-template")
                .templateVariables(Map.of("userName", "Test"))
                .priority(EmailNotificationRequestedEvent.NotificationPriority.HIGH)
                .requestedBy("system")
                .build();

        // Then
        assertThat(event).isNotNull();
        assertThat(event.tenantId()).isEqualTo("tenant-1");
        assertThat(event.recipientEmail()).isEqualTo("test@example.com");
        assertThat(event.subject()).isEqualTo("Welcome Email");
        assertThat(event.priority()).isEqualTo(EmailNotificationRequestedEvent.NotificationPriority.HIGH);
    }

    @Test
    void shouldValidateRequiredFields() {
        // When/Then
        assertThatThrownBy(() ->
                EmailNotificationRequestedEvent.builder()
                        .tenantId("tenant-1")
                        .build()
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("recipientEmail");
    }

    @Test
    void shouldValidateEmailFormat() {
        // When/Then
        assertThatThrownBy(() ->
                EmailNotificationRequestedEvent.builder()
                        .tenantId("tenant-1")
                        .recipientEmail("invalid-email")
                        .recipientName("Test")
                        .subject("Test")
                        .templateId("template")
                        .requestedBy("system")
                        .build()
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not valid");
    }

    @Test
    void shouldSupportAttachments() {
        // Given
        EmailNotificationRequestedEvent.EmailAttachment attachment =
                new EmailNotificationRequestedEvent.EmailAttachment(
                        "document.pdf",
                        "application/pdf",
                        1024L,
                        "https://example.com/document.pdf"
                );

        // When
        EmailNotificationRequestedEvent event = EmailNotificationRequestedEvent.builder()
                .tenantId("tenant-1")
                .recipientEmail("test@example.com")
                .recipientName("Test User")
                .subject("Document Attached")
                .templateId("document-template")
                .attachments(List.of(attachment))
                .requestedBy("system")
                .build();

        // Then
        assertThat(event.attachments()).hasSize(1);
        assertThat(event.attachments().get(0).fileName()).isEqualTo("document.pdf");
    }
}