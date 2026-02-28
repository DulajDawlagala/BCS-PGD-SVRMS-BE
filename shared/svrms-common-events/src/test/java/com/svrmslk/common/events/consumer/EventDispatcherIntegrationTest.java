package com.svrmslk.common.events.consumer;

import com.svrmslk.common.events.KafkaTestContainerBase;
import com.svrmslk.common.events.api.EventHandler;
import com.svrmslk.common.events.core.EventEnvelope;
import com.svrmslk.common.events.domain.auth.UserRegisteredEvent;
import com.svrmslk.common.events.exception.EventConsumeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration tests for EventDispatcher.
 *
 * @author Platform Team
 * @since 1.0.0
 */
class EventDispatcherIntegrationTest extends KafkaTestContainerBase {

    private EventDispatcher dispatcher;

    @BeforeEach
    void setUp() {
        dispatcher = new EventDispatcher();
    }

    @Test
    void shouldDispatchEventToRegisteredHandler() {
        // Given
        AtomicInteger handlerCallCount = new AtomicInteger(0);

        EventHandler<UserRegisteredEvent> handler = envelope -> {
            handlerCallCount.incrementAndGet();
            assertThat(envelope.payload().userId()).isEqualTo("user-123");
        };

        dispatcher.registerHandler("user.registered", handler);

        UserRegisteredEvent event = new UserRegisteredEvent(
                "user-123",
                "test@example.com",
                "tenant-1",
                "web"
        );
        EventEnvelope<UserRegisteredEvent> envelope = EventEnvelope.wrap(event);

        // When
        dispatcher.dispatch(envelope);

        // Then
        assertThat(handlerCallCount.get()).isEqualTo(1);
    }

    @Test
    void shouldDispatchToMultipleHandlers() {
        // Given
        AtomicInteger handler1CallCount = new AtomicInteger(0);
        AtomicInteger handler2CallCount = new AtomicInteger(0);

        EventHandler<UserRegisteredEvent> handler1 = envelope -> handler1CallCount.incrementAndGet();
        EventHandler<UserRegisteredEvent> handler2 = envelope -> handler2CallCount.incrementAndGet();

        dispatcher.registerHandler("user.registered", handler1);
        dispatcher.registerHandler("user.registered", handler2);

        UserRegisteredEvent event = new UserRegisteredEvent(
                "user-123",
                "test@example.com",
                "tenant-1",
                "web"
        );
        EventEnvelope<UserRegisteredEvent> envelope = EventEnvelope.wrap(event);

        // When
        dispatcher.dispatch(envelope);

        // Then
        assertThat(handler1CallCount.get()).isEqualTo(1);
        assertThat(handler2CallCount.get()).isEqualTo(1);
    }

    @Test
    void shouldNotFailIfNoHandlerRegistered() {
        // Given
        UserRegisteredEvent event = new UserRegisteredEvent(
                "user-123",
                "test@example.com",
                "tenant-1",
                "web"
        );
        EventEnvelope<UserRegisteredEvent> envelope = EventEnvelope.wrap(event);

        // When/Then - should not throw exception
        dispatcher.dispatch(envelope);
    }

    @Test
    void shouldThrowExceptionIfAllHandlersFail() {
        // Given
        EventHandler<UserRegisteredEvent> handler1 = envelope -> {
            throw new RuntimeException("Handler 1 failed");
        };
        EventHandler<UserRegisteredEvent> handler2 = envelope -> {
            throw new RuntimeException("Handler 2 failed");
        };

        dispatcher.registerHandler("user.registered", handler1);
        dispatcher.registerHandler("user.registered", handler2);

        UserRegisteredEvent event = new UserRegisteredEvent(
                "user-123",
                "test@example.com",
                "tenant-1",
                "web"
        );
        EventEnvelope<UserRegisteredEvent> envelope = EventEnvelope.wrap(event);

        // When/Then
        assertThatThrownBy(() -> dispatcher.dispatch(envelope))
                .isInstanceOf(EventConsumeException.class)
                .hasMessageContaining("All handlers failed");
    }

    @Test
    void shouldSucceedIfAtLeastOneHandlerSucceeds() {
        // Given
        AtomicInteger successHandlerCallCount = new AtomicInteger(0);

        EventHandler<UserRegisteredEvent> failingHandler = envelope -> {
            throw new RuntimeException("Handler failed");
        };
        EventHandler<UserRegisteredEvent> successHandler = envelope -> {
            successHandlerCallCount.incrementAndGet();
        };

        dispatcher.registerHandler("user.registered", failingHandler);
        dispatcher.registerHandler("user.registered", successHandler);

        UserRegisteredEvent event = new UserRegisteredEvent(
                "user-123",
                "test@example.com",
                "tenant-1",
                "web"
        );
        EventEnvelope<UserRegisteredEvent> envelope = EventEnvelope.wrap(event);

        // When - should not throw
        dispatcher.dispatch(envelope);

        // Then
        assertThat(successHandlerCallCount.get()).isEqualTo(1);
    }

    @Test
    void shouldUnregisterHandler() {
        // Given
        AtomicInteger handlerCallCount = new AtomicInteger(0);
        EventHandler<UserRegisteredEvent> handler = envelope -> handlerCallCount.incrementAndGet();

        dispatcher.registerHandler("user.registered", handler);
        assertThat(dispatcher.getHandlerCount("user.registered")).isEqualTo(1);

        // When
        dispatcher.unregisterHandler("user.registered", handler);

        // Then
        assertThat(dispatcher.getHandlerCount("user.registered")).isEqualTo(0);

        UserRegisteredEvent event = new UserRegisteredEvent(
                "user-123",
                "test@example.com",
                "tenant-1",
                "web"
        );
        EventEnvelope<UserRegisteredEvent> envelope = EventEnvelope.wrap(event);

        dispatcher.dispatch(envelope);
        assertThat(handlerCallCount.get()).isEqualTo(0);
    }
}