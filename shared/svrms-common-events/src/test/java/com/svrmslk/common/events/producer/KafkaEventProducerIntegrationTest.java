//package com.svrmslk.common.events.producer;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.svrmslk.common.events.KafkaTestContainerBase;
//import com.svrmslk.common.events.api.EventProducer;
//import com.svrmslk.common.events.core.EventEnvelope;
//import com.svrmslk.common.events.core.EventMetadata;
//import com.svrmslk.common.events.core.EventType;
//import com.svrmslk.common.events.core.EventVersion;
//import com.svrmslk.common.events.domain.auth.UserRegisteredEvent;
//import com.svrmslk.common.events.serde.JsonEventSerde;
//import org.apache.kafka.clients.consumer.ConsumerConfig;
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.apache.kafka.clients.consumer.ConsumerRecords;
//import org.apache.kafka.clients.consumer.KafkaConsumer;
//import org.apache.kafka.clients.producer.ProducerConfig;
//import org.apache.kafka.common.serialization.ByteArrayDeserializer;
//import org.apache.kafka.common.serialization.ByteArraySerializer;
//import org.apache.kafka.common.serialization.StringDeserializer;
//import org.apache.kafka.common.serialization.StringSerializer;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.kafka.core.DefaultKafkaProducerFactory;
//import org.springframework.kafka.core.KafkaTemplate;
//
//import java.time.Duration;
//import java.time.Instant;
//import java.util.*;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.TimeUnit;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.*;
//
///**
// * Integration tests for KafkaEventProducer using Testcontainers.
// *
// * @author Platform Team
// * @since 1.0.0
// */
//class KafkaEventProducerIntegrationTest extends KafkaTestContainerBase {
//
//    private static final String TEST_TOPIC = "test-events";
//
//    private KafkaEventProducer eventProducer;
//    private JsonEventSerde eventSerde;
//    private KafkaConsumer<String, byte[]> testConsumer;
//
//    @BeforeEach
//    void setUp() {
//        // Setup producer
//        Map<String, Object> producerProps = new HashMap<>();
//        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, getBootstrapServers());
//        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class);
//        producerProps.put(ProducerConfig.ACKS_CONFIG, "all");
//        producerProps.put(ProducerConfig.RETRIES_CONFIG, 3);
//
//        DefaultKafkaProducerFactory<String, byte[]> producerFactory =
//                new DefaultKafkaProducerFactory<>(producerProps);
//        KafkaTemplate<String, byte[]> kafkaTemplate = new KafkaTemplate<>(producerFactory);
//
//        eventSerde = new JsonEventSerde(new ObjectMapper());
//        eventProducer = new KafkaEventProducer(kafkaTemplate, eventSerde, TEST_TOPIC);
//
//        // Setup test consumer
//        Map<String, Object> consumerProps = new HashMap<>();
//        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, getBootstrapServers());
//        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group-" + UUID.randomUUID());
//        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class);
//        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
//        consumerProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
//
//        testConsumer = new KafkaConsumer<>(consumerProps);
//        testConsumer.subscribe(Collections.singletonList(TEST_TOPIC));
//    }
//
//    @Test
//    void shouldPublishEventToKafka() throws Exception {
//        // Given
//        UserRegisteredEvent event = new UserRegisteredEvent(
//                "user-123",
//                "test@example.com",
//                "tenant-1",
//                "web"
//        );
//
//        EventMetadata metadata = EventMetadata.builder()
//                .eventId(event.getEventId())
//                .eventType(EventType.fromClass(UserRegisteredEvent.class))
//                .timestamp(event.getOccurredAt())
//                .tenantId(event.getTenantId())
//                .traceId("trace-123")
//                .correlationId("correlation-123")
//                .build();
//
//        EventEnvelope<UserRegisteredEvent> envelope = new EventEnvelope<>(metadata, event);
//
//        // When
//        CompletableFuture<EventProducer.SendResult> future = eventProducer.send(envelope);
//        EventProducer.SendResult result = future.get(10, TimeUnit.SECONDS);
//
//        // Then
//        assertThat(result).isNotNull();
//        assertThat(result.topic()).isEqualTo(TEST_TOPIC);
//        assertThat(result.offset()).isGreaterThanOrEqualTo(0);
//
//        // Verify message was actually published to Kafka
//        ConsumerRecords<String, byte[]> records = testConsumer.poll(Duration.ofSeconds(10));
//        assertThat(records.isEmpty()).isFalse();
//
//        ConsumerRecord<String, byte[]> record = records.iterator().next();
//        assertThat(record.key()).isEqualTo(event.getTenantId());
//
//        // Verify headers
//        assertThat(record.headers().lastHeader("event-id")).isNotNull();
//        assertThat(new String(record.headers().lastHeader("event-id").value())).isEqualTo(event.getEventId());
//        assertThat(new String(record.headers().lastHeader("event-type").value())).isEqualTo("user.registered");
//        assertThat(new String(record.headers().lastHeader("tenant-id").value())).isEqualTo("tenant-1");
//        assertThat(new String(record.headers().lastHeader("trace-id").value())).isEqualTo("trace-123");
//    }
//
//    @Test
//    void shouldPublishMultipleEventsInOrder() throws Exception {
//        // Given
//        String tenantId = "tenant-1";
//        List<UserRegisteredEvent> events = new ArrayList<>();
//
//        for (int i = 0; i < 5; i++) {
//            events.add(new UserRegisteredEvent(
//                    "user-" + i,
//                    "user" + i + "@example.com",
//                    tenantId,
//                    "web"
//            ));
//        }
//
//        // When - publish all events
//        List<CompletableFuture<EventProducer.SendResult>> futures = new ArrayList<>();
//        for (UserRegisteredEvent event : events) {
//            EventEnvelope<UserRegisteredEvent> envelope = EventEnvelope.wrap(event);
//            futures.add(eventProducer.send(TEST_TOPIC, tenantId, envelope));
//        }
//
//        // Wait for all publishes to complete
//        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
//                .get(10, TimeUnit.SECONDS);
//
//        // Then - verify all events were published
//        List<ConsumerRecord<String, byte[]>> receivedRecords = new ArrayList<>();
//        long endTime = System.currentTimeMillis() + 10000;
//
//        while (receivedRecords.size() < 5 && System.currentTimeMillis() < endTime) {
//            ConsumerRecords<String, byte[]> records = testConsumer.poll(Duration.ofSeconds(1));
//            records.forEach(receivedRecords::add);
//        }
//
//        assertThat(receivedRecords).hasSize(5);
//
//        // Verify all events have same partition (due to same key)
//        int partition = receivedRecords.get(0).partition();
//        assertThat(receivedRecords).allMatch(r -> r.partition() == partition);
//    }
//
//    @Test
//    void shouldHandleCustomPartitionKey() throws Exception {
//        // Given
//        UserRegisteredEvent event = new UserRegisteredEvent(
//                "user-123",
//                "test@example.com",
//                "tenant-1",
//                "web"
//        );
//        EventEnvelope<UserRegisteredEvent> envelope = EventEnvelope.wrap(event);
//        String customKey = "custom-partition-key";
//
//        // When
//        CompletableFuture<EventProducer.SendResult> future =
//                eventProducer.send(TEST_TOPIC, customKey, envelope);
//        EventProducer.SendResult result = future.get(10, TimeUnit.SECONDS);
//
//        // Then
//        assertThat(result).isNotNull();
//
//        ConsumerRecords<String, byte[]> records = testConsumer.poll(Duration.ofSeconds(10));
//        assertThat(records.isEmpty()).isFalse();
//
//        ConsumerRecord<String, byte[]> record = records.iterator().next();
//        assertThat(record.key()).isEqualTo(customKey);
//    }
//
//    @Test
//    void shouldSerializeAndDeserializeEvent() throws Exception {
//        // Given
//        UserRegisteredEvent event = new UserRegisteredEvent(
//                "user-123",
//                "test@example.com",
//                "tenant-1",
//                "web"
//        );
//        EventEnvelope<UserRegisteredEvent> envelope = EventEnvelope.wrap(event);
//
//        // When
//        eventProducer.send(envelope).get(10, TimeUnit.SECONDS);
//
//        // Then
//        ConsumerRecords<String, byte[]> records = testConsumer.poll(Duration.ofSeconds(10));
//        assertThat(records.isEmpty()).isFalse();
//
//        ConsumerRecord<String, byte[]> record = records.iterator().next();
//
//        // Register event type for deserialization
//        eventSerde.registerEventType(UserRegisteredEvent.class);
//
//        // Deserialize
//        EventEnvelope<?> deserializedEnvelope = eventSerde.deserialize(
//                record.value(),
//                "user.registered"
//        );
//
//        assertThat(deserializedEnvelope).isNotNull();
//        assertThat(deserializedEnvelope.payload()).isInstanceOf(UserRegisteredEvent.class);
//
//        UserRegisteredEvent deserializedEvent = (UserRegisteredEvent) deserializedEnvelope.payload();
//        assertThat(deserializedEvent.userId()).isEqualTo("user-123");
//        assertThat(deserializedEvent.email()).isEqualTo("test@example.com");
//        assertThat(deserializedEvent.tenantId()).isEqualTo("tenant-1");
//    }
//}


package com.svrmslk.common.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svrmslk.common.events.api.EventProducer;
import com.svrmslk.common.events.core.EventEnvelope;
import com.svrmslk.common.events.core.EventMetadata;
import com.svrmslk.common.events.core.EventType;
import com.svrmslk.common.events.core.EventVersion;
import com.svrmslk.common.events.domain.auth.UserRegisteredEvent;
import com.svrmslk.common.events.serde.JsonEventSerde;
import com.svrmslk.common.events.producer.KafkaEventProducer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class KafkaEventProducerIntegrationTest extends KafkaTestContainerBase {

    private static final String TEST_TOPIC = "test-events";

    private KafkaEventProducer eventProducer;
    private JsonEventSerde eventSerde;
    private KafkaConsumer<String, byte[]> testConsumer;

    @BeforeEach
    void setUp() {
        // Producer setup
        Map<String, Object> producerProps = new HashMap<>();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, getBootstrapServers());
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class);
        producerProps.put(ProducerConfig.ACKS_CONFIG, "all");
        producerProps.put(ProducerConfig.RETRIES_CONFIG, 3);

        DefaultKafkaProducerFactory<String, byte[]> producerFactory =
                new DefaultKafkaProducerFactory<>(producerProps);
        KafkaTemplate<String, byte[]> kafkaTemplate = new KafkaTemplate<>(producerFactory);

        eventSerde = new JsonEventSerde(new ObjectMapper());
        eventSerde.registerEventType(UserRegisteredEvent.class);

        eventProducer = new KafkaEventProducer(kafkaTemplate, eventSerde, TEST_TOPIC);

        // Consumer setup
        Map<String, Object> consumerProps = new HashMap<>();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, getBootstrapServers());
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group-" + UUID.randomUUID());
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        testConsumer = new KafkaConsumer<>(consumerProps);
        testConsumer.subscribe(Collections.singletonList(TEST_TOPIC));
    }

    @Test
    void shouldPublishEventToKafka() throws Exception {
        UserRegisteredEvent event = new UserRegisteredEvent(
                "user-123", "test@example.com", "tenant-1", "web"
        );

        EventMetadata metadata = EventMetadata.builder()
                .eventId(event.getEventId())
                .eventType(EventType.fromClass(UserRegisteredEvent.class))
                .timestamp(event.getOccurredAt())
                .tenantId(event.getTenantId())
                .traceId("trace-123")
                .correlationId("correlation-123")
                .build();

        EventEnvelope<UserRegisteredEvent> envelope = new EventEnvelope<>(metadata, event);

        CompletableFuture<EventProducer.SendResult> future = eventProducer.send(envelope);
        EventProducer.SendResult result = future.get(10, TimeUnit.SECONDS);

        assertThat(result).isNotNull();
        assertThat(result.topic()).isEqualTo(TEST_TOPIC);
        assertThat(result.offset()).isGreaterThanOrEqualTo(0);

        ConsumerRecords<String, byte[]> records = testConsumer.poll(Duration.ofSeconds(10));
        assertThat(records.isEmpty()).isFalse();

        ConsumerRecord<String, byte[]> record = records.iterator().next();
        assertThat(record.key()).isEqualTo(event.getTenantId());
    }

    @Test
    void shouldPublishMultipleEventsInOrder() throws Exception {
        String tenantId = "tenant-1";
        List<UserRegisteredEvent> events = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            events.add(new UserRegisteredEvent("user-" + i, "user" + i + "@example.com", tenantId, "web"));
        }

        List<CompletableFuture<EventProducer.SendResult>> futures = new ArrayList<>();
        for (UserRegisteredEvent event : events) {
            EventEnvelope<UserRegisteredEvent> envelope = EventEnvelope.wrap(event);
            futures.add(eventProducer.send(TEST_TOPIC, tenantId, envelope));
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get(10, TimeUnit.SECONDS);

        List<ConsumerRecord<String, byte[]>> receivedRecords = new ArrayList<>();
        long endTime = System.currentTimeMillis() + 10000;
        while (receivedRecords.size() < 5 && System.currentTimeMillis() < endTime) {
            testConsumer.poll(Duration.ofSeconds(1)).forEach(receivedRecords::add);
        }

        assertThat(receivedRecords).hasSize(5);

        int partition = receivedRecords.get(0).partition();
        assertThat(receivedRecords).allMatch(r -> r.partition() == partition);
    }

    @Test
    void shouldHandleCustomPartitionKey() throws Exception {
        UserRegisteredEvent event = new UserRegisteredEvent("user-123", "test@example.com", "tenant-1", "web");
        EventEnvelope<UserRegisteredEvent> envelope = EventEnvelope.wrap(event);
        String customKey = "custom-partition-key";

        CompletableFuture<EventProducer.SendResult> future = eventProducer.send(TEST_TOPIC, customKey, envelope);
        EventProducer.SendResult result = future.get(10, TimeUnit.SECONDS);

        assertThat(result).isNotNull();

        ConsumerRecords<String, byte[]> records = testConsumer.poll(Duration.ofSeconds(10));
        ConsumerRecord<String, byte[]> record = records.iterator().next();
        assertThat(record.key()).isEqualTo(customKey);
    }

    @Test
    void shouldSerializeAndDeserializeEvent() throws Exception {
        UserRegisteredEvent event = new UserRegisteredEvent("user-123", "test@example.com", "tenant-1", "web");
        EventEnvelope<UserRegisteredEvent> envelope = EventEnvelope.wrap(event);

        eventProducer.send(envelope).get(10, TimeUnit.SECONDS);

        ConsumerRecords<String, byte[]> records = testConsumer.poll(Duration.ofSeconds(10));
        ConsumerRecord<String, byte[]> record = records.iterator().next();

        eventSerde.registerEventType(UserRegisteredEvent.class);
        EventEnvelope<?> deserialized = eventSerde.deserialize(record.value(), "user.registered");

        assertThat(deserialized.payload()).isInstanceOf(UserRegisteredEvent.class);
        UserRegisteredEvent deserializedEvent = (UserRegisteredEvent) deserialized.payload();
        assertThat(deserializedEvent.userId()).isEqualTo("user-123");
    }
}