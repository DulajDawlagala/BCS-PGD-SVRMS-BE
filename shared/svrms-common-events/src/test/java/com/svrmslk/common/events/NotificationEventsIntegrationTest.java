//package com.svrmslk.common.events;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.svrmslk.common.events.api.EventBus;
//import com.svrmslk.common.events.api.EventHandler;
//import com.svrmslk.common.events.consumer.EventDispatcher;
//import com.svrmslk.common.events.domain.notification.EmailNotificationRequestedEvent;
//import com.svrmslk.common.events.domain.notification.InAppNotificationRequestedEvent;
//import com.svrmslk.common.events.serde.JsonEventSerde;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.util.Map;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.atomic.AtomicReference;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
///**
// * Integration tests for notification domain events.
// */
//class NotificationEventsIntegrationTest extends KafkaTestContainerBase {
//
//    private EventDispatcher dispatcher;
//    private JsonEventSerde eventSerde;
//
//    @BeforeEach
//    void setUp() {
//        dispatcher = new EventDispatcher();
//        eventSerde = new JsonEventSerde(new ObjectMapper());
//
//        // Register event types
//        eventSerde.registerEventType(EmailNotificationRequestedEvent.class);
//        eventSerde.registerEventType(InAppNotificationRequestedEvent.class);
//    }
//
//    @Test
//    void shouldSerializeAndDeserializeEmailNotificationEvent() throws Exception {
//        // Given
//        EmailNotificationRequestedEvent event = EmailNotificationRequestedEvent.builder()
//                .tenantId("tenant-1")
//                .recipientEmail("test@example.com")
//                .recipientName("Test User")
//                .subject("Test Email")
//                .templateId("test-template")
//                .templateVariables(Map.of("key", "value"))
//                .requestedBy("system")
//                .build();
//
//        // When
//        byte[] serialized = eventSerde.serialize(com.svrmslk.common.events.core.EventEnvelope.wrap(event));
//        var deserialized = eventSerde.deserialize(serialized, "email.notification.requested");
//
//        // Then
//        assertThat(deserialized.payload()).isInstanceOf(EmailNotificationRequestedEvent.class);
//        EmailNotificationRequestedEvent deserializedEvent = (EmailNotificationRequestedEvent) deserialized.payload();
//        assertThat(deserializedEvent.recipientEmail()).isEqualTo("test@example.com");
//        assertThat(deserializedEvent.subject()).isEqualTo("Test Email");
//    }
//
//    @Test
//    void shouldDispatchEmailNotificationEvent() throws Exception {
//        // Given
//        CountDownLatch latch = new CountDownLatch(1);
//        AtomicReference<EmailNotificationRequestedEvent> received = new AtomicReference<>();
//
//        EventHandler<EmailNotificationRequestedEvent> handler = envelope -> {
//            received.set(envelope.payload());
//            latch.countDown();
//        };
//
//        dispatcher.registerHandler("email.notification.requested", handler);
//
//        EmailNotificationRequestedEvent event = EmailNotificationRequestedEvent.builder()
//                .tenantId("tenant-1")
//                .recipientEmail("test@example.com")
//                .recipientName("Test User")
//                .subject("Test Subject")
//                .templateId("test-template")
//                .requestedBy("system")
//                .build();
//
//        // When
//        dispatcher.dispatch(com.svrmslk.common.events.core.EventEnvelope.wrap(event));
//
//        // Then
//        boolean completed = latch.await(5, TimeUnit.SECONDS);
//        assertThat(completed).isTrue();
//        assertThat(received.get()).isNotNull();
//        assertThat(received.get().recipientEmail()).isEqualTo("test@example.com");
//    }
//
//    @Test
//    void shouldDispatchInAppNotificationEvent() throws Exception {
//        // Given
//        CountDownLatch latch = new CountDownLatch(1);
//        AtomicReference<InAppNotificationRequestedEvent> received = new AtomicReference<>();
//
//        EventHandler<InAppNotificationRequestedEvent> handler = envelope -> {
//            received.set(envelope.payload());
//            latch.countDown();
//        };
//
//        dispatcher.registerHandler("in.app.notification.requested", handler);
//        InAppNotificationRequestedEvent event = InAppNotificationRequestedEvent.builder()
//                .tenantId("tenant-1")
//                .userId("user-123")
//                .title("Test Notification")
//                .message("This is a test notification")
//                .notificationType(InAppNotificationRequestedEvent.NotificationType.INFO)
//                .requestedBy("system")
//                .build();
//
//        // When
//        dispatcher.dispatch(com.svrmslk.common.events.core.EventEnvelope.wrap(event));
//
//        // Then
//        boolean completed = latch.await(5, TimeUnit.SECONDS);
//        assertThat(completed).isTrue();
//        assertThat(received.get()).isNotNull();
//        assertThat(received.get().title()).isEqualTo("Test Notification");
//    }
//}

package com.svrmslk.common.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svrmslk.common.events.api.EventHandler;
import com.svrmslk.common.events.consumer.EventDispatcher;
import com.svrmslk.common.events.domain.notification.EmailNotificationRequestedEvent;
import com.svrmslk.common.events.domain.notification.InAppNotificationRequestedEvent;
import com.svrmslk.common.events.serde.JsonEventSerde;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.svrmslk.common.events.core.EventEnvelope;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationEventsIntegrationTest extends KafkaTestContainerBase {

    private EventDispatcher dispatcher;
    private JsonEventSerde eventSerde;

    @BeforeEach
    void setUp() {
        dispatcher = new EventDispatcher();
        eventSerde = new JsonEventSerde(new ObjectMapper());

        eventSerde.registerEventType(EmailNotificationRequestedEvent.class);
        eventSerde.registerEventType(InAppNotificationRequestedEvent.class);
    }

    @Test
    void shouldSerializeAndDeserializeEmailNotificationEvent() throws Exception {
        EmailNotificationRequestedEvent event = EmailNotificationRequestedEvent.builder()
                .tenantId("tenant-1")
                .recipientEmail("test@example.com")
                .recipientName("Test User")
                .subject("Test Email")
                .templateId("test-template")
                .templateVariables(Map.of("key", "value"))
                .requestedBy("system")
                .build();

        byte[] serialized = eventSerde.serialize(EventEnvelope.<EmailNotificationRequestedEvent>wrap(event));
        var deserialized = eventSerde.deserialize(serialized, "email.notification.requested");

        assertThat(deserialized.payload()).isInstanceOf(EmailNotificationRequestedEvent.class);
        assertThat(((EmailNotificationRequestedEvent) deserialized.payload()).recipientEmail()).isEqualTo("test@example.com");
    }

    @Test
    void shouldDispatchEmailNotificationEvent() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<EmailNotificationRequestedEvent> received = new AtomicReference<>();

        EventHandler<EmailNotificationRequestedEvent> handler = envelope -> {
            received.set(envelope.payload());
            latch.countDown();
        };
        dispatcher.registerHandler("email.notification.requested", handler);

        EmailNotificationRequestedEvent event = EmailNotificationRequestedEvent.builder()
                .tenantId("tenant-1")
                .recipientEmail("test@example.com")
                .recipientName("Test User")
                .subject("Test Subject")
                .templateId("test-template")
                .requestedBy("system")
                .build();

        dispatcher.dispatch(EventEnvelope.<EmailNotificationRequestedEvent>wrap(event));

        assertThat(latch.await(5, TimeUnit.SECONDS)).isTrue();
        assertThat(received.get().recipientEmail()).isEqualTo("test@example.com");
    }

    @Test
    void shouldDispatchInAppNotificationEvent() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<InAppNotificationRequestedEvent> received = new AtomicReference<>();

        EventHandler<InAppNotificationRequestedEvent> handler = envelope -> {
            received.set(envelope.payload());
            latch.countDown();
        };
        dispatcher.registerHandler("in.app.notification.requested", handler);

        InAppNotificationRequestedEvent event = InAppNotificationRequestedEvent.builder()
                .tenantId("tenant-1")
                .userId("user-123")
                .title("Test Notification")
                .message("This is a test notification")
                .notificationType(InAppNotificationRequestedEvent.NotificationType.INFO)
                .requestedBy("system")
                .build();

        dispatcher.dispatch(EventEnvelope.<InAppNotificationRequestedEvent>wrap(event));

        assertThat(latch.await(5, TimeUnit.SECONDS)).isTrue();
        assertThat(received.get().title()).isEqualTo("Test Notification");
    }
}