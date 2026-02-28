package com.svrmslk.common.events.producer;

import com.svrmslk.common.events.KafkaTestContainerBase;
import com.svrmslk.common.events.api.EventProducer;
import com.svrmslk.common.events.core.EventEnvelope;
import com.svrmslk.common.events.domain.auth.UserRegisteredEvent;
import com.svrmslk.common.events.exception.EventPublishException;
import io.github.resilience4j.retry.RetryConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Integration tests for RetryableEventProducer.
 *
 * @author Platform Team
 * @since 1.0.0
 */
class RetryableEventProducerIntegrationTest extends KafkaTestContainerBase {

    private EventProducer mockDelegate;
    private RetryableEventProducer retryableProducer;

    @BeforeEach
    void setUp() {
        mockDelegate = Mockito.mock(EventProducer.class);

        RetryConfig retryConfig = RetryConfig.custom()
                .maxAttempts(3)
                .waitDuration(Duration.ofMillis(10))
                .retryOnException(ex -> ex instanceof EventPublishException)
                .build();

        retryableProducer = new RetryableEventProducer(mockDelegate, retryConfig);
    }

    @Test
    void shouldRetryOnTransientFailure() {
        // Given
        UserRegisteredEvent event = new UserRegisteredEvent(
                "user-123",
                "test@example.com",
                "tenant-1",
                "web"
        );
        EventEnvelope<UserRegisteredEvent> envelope = EventEnvelope.wrap(event);

        AtomicInteger attemptCount = new AtomicInteger(0);
        EventProducer.SendResult successResult = new EventProducer.SendResult(
                "test-topic", "0", 100L, System.currentTimeMillis()
        );

        // Fail twice, then succeed
        when(mockDelegate.send(any(EventEnvelope.class)))
                .thenAnswer(invocation -> {
                    int attempt = attemptCount.incrementAndGet();
                    if (attempt < 3) {
                        return CompletableFuture.failedFuture(
                                new EventPublishException("Transient failure",
                                        new org.apache.kafka.common.errors.TimeoutException())
                        );
                    }
                    return CompletableFuture.completedFuture(successResult);
                });

        // When
        CompletableFuture<EventProducer.SendResult> result = retryableProducer.send(envelope);

        // Then
        assertThat(result).succeedsWithin(Duration.ofSeconds(5));
        assertThat(attemptCount.get()).isEqualTo(3);
        verify(mockDelegate, times(3)).send(any(EventEnvelope.class));
    }

    @Test
    void shouldFailAfterMaxRetries() {
        // Given
        UserRegisteredEvent event = new UserRegisteredEvent(
                "user-123",
                "test@example.com",
                "tenant-1",
                "web"
        );
        EventEnvelope<UserRegisteredEvent> envelope = EventEnvelope.wrap(event);

        // Always fail
        when(mockDelegate.send(any(EventEnvelope.class)))
                .thenReturn(CompletableFuture.failedFuture(
                        new EventPublishException("Persistent failure",
                                new org.apache.kafka.common.errors.TimeoutException())
                ));

        // When/Then
        CompletableFuture<EventProducer.SendResult> result = retryableProducer.send(envelope);

        assertThatThrownBy(() -> result.get())
                .hasCauseInstanceOf(EventPublishException.class);

        verify(mockDelegate, times(3)).send(any(EventEnvelope.class));
    }

    @Test
    void shouldNotRetryOnNonRetryableException() {
        // Given
        UserRegisteredEvent event = new UserRegisteredEvent(
                "user-123",
                "test@example.com",
                "tenant-1",
                "web"
        );
        EventEnvelope<UserRegisteredEvent> envelope = EventEnvelope.wrap(event);

        // Fail with non-retryable exception (no cause)
        when(mockDelegate.send(any(EventEnvelope.class)))
                .thenReturn(CompletableFuture.failedFuture(
                        new EventPublishException("Non-retryable failure")
                ));

        // When/Then
        CompletableFuture<EventProducer.SendResult> result = retryableProducer.send(envelope);

        assertThatThrownBy(() -> result.get())
                .hasCauseInstanceOf(EventPublishException.class);

        // Should only try once
        verify(mockDelegate, times(1)).send(any(EventEnvelope.class));
    }

    @Test
    void shouldSucceedOnFirstAttempt() {
        // Given
        UserRegisteredEvent event = new UserRegisteredEvent(
                "user-123",
                "test@example.com",
                "tenant-1",
                "web"
        );
        EventEnvelope<UserRegisteredEvent> envelope = EventEnvelope.wrap(event);

        EventProducer.SendResult successResult = new EventProducer.SendResult(
                "test-topic", "0", 100L, System.currentTimeMillis()
        );

        when(mockDelegate.send(any(EventEnvelope.class)))
                .thenReturn(CompletableFuture.completedFuture(successResult));

        // When
        CompletableFuture<EventProducer.SendResult> result = retryableProducer.send(envelope);

        // Then
        assertThat(result).succeedsWithin(Duration.ofSeconds(1));
        verify(mockDelegate, times(1)).send(any(EventEnvelope.class));
    }
}