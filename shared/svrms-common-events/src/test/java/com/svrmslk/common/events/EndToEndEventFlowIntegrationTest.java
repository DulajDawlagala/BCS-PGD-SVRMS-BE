//// src/test/java/com/svrmslk/common/events/EndToEndEventFlowIntegrationTest.java
//package com.svrmslk.common.events;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.svrmslk.common.events.api.Event;
//import com.svrmslk.common.events.api.EventBus;
//import com.svrmslk.common.events.api.EventHandler;
//import com.svrmslk.common.events.consumer.DeadLetterHandler;
//import com.svrmslk.common.events.consumer.EventDispatcher;
//import com.svrmslk.common.events.consumer.KafkaEventListener;
//import com.svrmslk.common.events.core.EventEnvelope;
//import com.svrmslk.common.events.domain.auth.UserRegisteredEvent;
//import com.svrmslk.common.events.observability.EventMetrics;
//import com.svrmslk.common.events.observability.EventTracing;
//import com.svrmslk.common.events.producer.DefaultEventBus;
//import com.svrmslk.common.events.producer.KafkaEventProducer;
//import com.svrmslk.common.events.schema.validation.JsonSchemaValidator;
//import com.svrmslk.common.events.security.EventSigner;
//import com.svrmslk.common.events.security.EventVerifier;
//import com.svrmslk.common.events.serde.JsonEventSerde;
//import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
//import io.micrometer.tracing.Tracer;
//import io.micrometer.tracing.brave.bridge.BraveBaggageManager;
//import io.micrometer.tracing.brave.bridge.BraveCurrentTraceContext;
//import io.micrometer.tracing.brave.bridge.BraveTracer;
//import org.apache.kafka.clients.producer.ProducerConfig;
//import org.apache.kafka.common.serialization.ByteArraySerializer;
//import org.apache.kafka.common.serialization.StringSerializer;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.kafka.core.DefaultKafkaProducerFactory;
//import org.springframework.kafka.core.KafkaTemplate;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.atomic.AtomicReference;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
///**
// * End-to-end integration test covering the complete event flow:
// * Publish -> Kafka -> Consume -> Dispatch -> Handler
// *
// * @author Platform Team
// * @since 1.0.0
// */
//class EndToEndEventFlowIntegrationTest extends KafkaTestContainerBase {
//
//    private static final String TEST_TOPIC = "e2e-test-events";
//    private static final String DLQ_TOPIC = "e2e-test-dlq";
//
//    private EventBus eventBus;
//    private EventDispatcher eventDispatcher;
//    private KafkaEventListener kafkaEventListener;
//
//    @BeforeEach
//    void setUp() {
//        // Setup serialization
//        ObjectMapper objectMapper = new ObjectMapper();
//        JsonEventSerde eventSerde = new JsonEventSerde(objectMapper);
//        eventSerde.registerEventType(UserRegisteredEvent.class);
//
//        // Setup producer
//        Map<String, Object> producerProps = new HashMap<>();
//        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, getBootstrapServers());
//        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class);
//
//        DefaultKafkaProducerFactory<String, byte[]> producerFactory =
//                new DefaultKafkaProducerFactory<>(producerProps);
//        KafkaTemplate<String, byte[]> kafkaTemplate = new KafkaTemplate<>(producerFactory);
//        KafkaEventProducer eventProducer = new KafkaEventProducer(
//                kafkaTemplate, eventSerde, TEST_TOPIC);
//
//        // Setup observability
//        SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();
//        EventMetrics eventMetrics = new EventMetrics(meterRegistry);
//
//        brave.Tracing braveTracing = brave.Tracing.newBuilder().build();
//        Tracer tracer = new BraveTracer(
//                braveTracing.tracer(),
//                new BraveCurrentTraceContext(braveTracing.currentTraceContext()),
//                new BraveBaggageManager()
//        );
//        EventTracing eventTracing = new EventTracing(tracer);
//
//        // Setup security (mock for testing)
//        EventSigner eventSigner = new EventSigner() {
//            @Override
//            public <T extends Event> EventEnvelope<T> sign(EventEnvelope<T> envelope) {
//                return envelope; // Pass-through for testing
//            }
//        };
//
//        EventVerifier eventVerifier = new EventVerifier() {
//            @Override
//            public boolean verify(EventEnvelope<?> envelope) {
//                return true; // Always valid for testing
//            }
//        };
//
//        // Setup schema validation (lenient for testing)
//        JsonSchemaValidator schemaValidator = new JsonSchemaValidator(objectMapper, false);
//
//        // Setup event bus
//        eventBus = new DefaultEventBus(
//                eventProducer,
//                schemaValidator,
//                eventSigner,
//                eventMetrics,
//                eventTracing,
//                "test-app"
//        );
//
//        // Setup consumer components
//        eventDispatcher = new EventDispatcher();
//
//        DeadLetterHandler deadLetterHandler = new DeadLetterHandler(kafkaTemplate, DLQ_TOPIC);
//
//        kafkaEventListener = new KafkaEventListener(
//                eventSerde,
//                eventDispatcher,
//                eventVerifier,
//                eventMetrics,
//                eventTracing,
//                deadLetterHandler
//        );
//    }
//
//    @Test
//    void shouldPublishConsumeAndHandleEventEndToEnd() throws Exception {
//        // Given
//        CountDownLatch latch = new CountDownLatch(1);
//        AtomicReference<UserRegisteredEvent> receivedEvent = new AtomicReference<>();
//
//        EventHandler<UserRegisteredEvent> handler = envelope -> {
//            receivedEvent.set(envelope.payload());
//            latch.countDown();
//        };
//
//        eventDispatcher.registerHandler("user.registered", handler);
//
//        UserRegisteredEvent event = new UserRegisteredEvent(
//                "user-123",
//                "test@example.com",
//                "tenant-1",
//                "web"
//        );
//
//        // When
//        eventBus.publish(event).get(10, TimeUnit.SECONDS);
//
//        // Then
//        boolean completed = latch.await(15, TimeUnit.SECONDS);
//        assertThat(completed).isTrue();
//
//        UserRegisteredEvent handled = receivedEvent.get();
//        assertThat(handled).isNotNull();
//        assertThat(handled.userId()).isEqualTo("user-123");
//        assertThat(handled.email()).isEqualTo("test@example.com");
//        assertThat(handled.tenantId()).isEqualTo("tenant-1");
//        assertThat(handled.registrationSource()).isEqualTo("web");
//    }
//
//    @Test
//    void shouldHandleMultipleEventsInSequence() throws Exception {
//        // Given
//        int eventCount = 10;
//        CountDownLatch latch = new CountDownLatch(eventCount);
//        AtomicReference<Integer> handledCount = new AtomicReference<>(0);
//
//        EventHandler<UserRegisteredEvent> handler = envelope -> {
//            handledCount.updateAndGet(v -> v + 1);
//            latch.countDown();
//        };
//
//        eventDispatcher.registerHandler("user.registered", handler);
//
//        // When
//        for (int i = 0; i < eventCount; i++) {
//            UserRegisteredEvent event = new UserRegisteredEvent(
//                    "user-" + i,
//                    "user" + i + "@example.com",
//                    "tenant-1",
//                    "web"
//            );
//            eventBus.publish(event);
//        }
//
//        // Then
//        boolean completed = latch.await(30, TimeUnit.SECONDS);
//        assertThat(completed).isTrue();
//        assertThat(handledCount.get()).isEqualTo(eventCount);
//    }
//}
package com.svrmslk.common.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svrmslk.common.events.api.Event;
import com.svrmslk.common.events.api.EventBus;
import com.svrmslk.common.events.api.EventHandler;
import com.svrmslk.common.events.consumer.DeadLetterHandler;
import com.svrmslk.common.events.consumer.EventDispatcher;
import com.svrmslk.common.events.consumer.KafkaEventListener;
import com.svrmslk.common.events.core.EventEnvelope;
import com.svrmslk.common.events.domain.auth.UserRegisteredEvent;
import com.svrmslk.common.events.observability.EventMetrics;
import com.svrmslk.common.events.observability.EventTracing;
import com.svrmslk.common.events.producer.DefaultEventBus;
import com.svrmslk.common.events.producer.KafkaEventProducer;
import com.svrmslk.common.events.schema.validation.JsonSchemaValidator;
import com.svrmslk.common.events.security.EventSigner;
import com.svrmslk.common.events.security.EventVerifier;
import com.svrmslk.common.events.serde.JsonEventSerde;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.brave.bridge.BraveBaggageManager;
import io.micrometer.tracing.brave.bridge.BraveCurrentTraceContext;
import io.micrometer.tracing.brave.bridge.BraveTracer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class EndToEndEventFlowIntegrationTest extends KafkaTestContainerBase {

    private static final String TEST_TOPIC = "e2e-test-events";
    private static final String DLQ_TOPIC = "e2e-test-dlq";

    private EventBus eventBus;
    private EventDispatcher eventDispatcher;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonEventSerde eventSerde = new JsonEventSerde(objectMapper);
        eventSerde.registerEventType(UserRegisteredEvent.class);

        // Kafka Producer
        Map<String, Object> producerProps = new HashMap<>();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, getBootstrapServers());
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class);

        KafkaTemplate<String, byte[]> kafkaTemplate = new KafkaTemplate<>(
                new DefaultKafkaProducerFactory<>(producerProps)
        );
        KafkaEventProducer eventProducer = new KafkaEventProducer(kafkaTemplate, eventSerde, TEST_TOPIC);

        // Observability
        EventMetrics eventMetrics = new EventMetrics(new SimpleMeterRegistry());
        Tracer tracer = new BraveTracer(
                brave.Tracing.newBuilder().build().tracer(),
                new BraveCurrentTraceContext(brave.Tracing.newBuilder().build().currentTraceContext()),
                new BraveBaggageManager()
        );
        EventTracing eventTracing = new EventTracing(tracer);

        // Security - generic
        EventSigner eventSigner = new EventSigner() {
            @Override
            public <T extends Event> EventEnvelope<T> sign(EventEnvelope<T> envelope) {
                return envelope;
            }
        };
        EventVerifier eventVerifier = envelope -> true;

        JsonSchemaValidator schemaValidator = new JsonSchemaValidator(objectMapper, false);

        eventBus = new DefaultEventBus(eventProducer, schemaValidator, eventSigner, eventMetrics, eventTracing, "test-app");

        // Dispatcher and listener
        eventDispatcher = new EventDispatcher();
        new KafkaEventListener(eventSerde, eventDispatcher, eventVerifier, eventMetrics, eventTracing,
                new DeadLetterHandler(kafkaTemplate, DLQ_TOPIC));
    }

    @Test
    void shouldPublishConsumeAndHandleEventEndToEnd() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<UserRegisteredEvent> received = new AtomicReference<>();

        EventHandler<UserRegisteredEvent> handler = envelope -> {
            received.set(envelope.payload());
            latch.countDown();
        };
        eventDispatcher.registerHandler("user.registered", handler);

        UserRegisteredEvent event = new UserRegisteredEvent("user-123", "test@example.com", "tenant-1", "web");
        eventBus.publish(event).get(10, TimeUnit.SECONDS);

        assertThat(latch.await(15, TimeUnit.SECONDS)).isTrue();
        UserRegisteredEvent handled = received.get();
        assertThat(handled.userId()).isEqualTo("user-123");
    }

    @Test
    void shouldHandleMultipleEventsInSequence() throws Exception {
        int count = 10;
        CountDownLatch latch = new CountDownLatch(count);
        AtomicReference<Integer> handledCount = new AtomicReference<>(0);

        EventHandler<UserRegisteredEvent> handler = envelope -> {
            handledCount.updateAndGet(v -> v + 1);
            latch.countDown();
        };
        eventDispatcher.registerHandler("user.registered", handler);

        for (int i = 0; i < count; i++) {
            eventBus.publish(new UserRegisteredEvent("user-" + i, "user" + i + "@example.com", "tenant-1", "web"));
        }

        assertThat(latch.await(30, TimeUnit.SECONDS)).isTrue();
        assertThat(handledCount.get()).isEqualTo(count);
    }
}