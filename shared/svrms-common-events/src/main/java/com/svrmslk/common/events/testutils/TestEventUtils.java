package com.svrmslk.common.events.testutils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svrmslk.common.events.api.Event;
import com.svrmslk.common.events.consumer.EventDispatcher;
import com.svrmslk.common.events.producer.KafkaEventProducer;
import com.svrmslk.common.events.serde.JsonEventSerde;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper methods for creating common test objects like EventSerde, EventDispatcher, KafkaProducer.
 */
public final class TestEventUtils {

    private TestEventUtils() { }

    /**
     * Create a JsonEventSerde with all common events registered.
     */
    public static JsonEventSerde createEventSerde() {
        JsonEventSerde serde = new JsonEventSerde(new ObjectMapper());

        // Register all common event types
        serde.registerEventType(com.svrmslk.common.events.domain.auth.UserRegisteredEvent.class);
        serde.registerEventType(com.svrmslk.common.events.domain.notification.EmailNotificationRequestedEvent.class);
        serde.registerEventType(com.svrmslk.common.events.domain.notification.InAppNotificationRequestedEvent.class);

        return serde;
    }

    /**
     * Create a simple EventDispatcher for testing.
     */
    public static EventDispatcher createDispatcher() {
        return new EventDispatcher();
    }

    /**
     * Create a KafkaEventProducer for testing.
     *
     * @param bootstrapServers Kafka bootstrap servers
     * @param topic            Kafka topic name
     */
    public static KafkaEventProducer createKafkaProducer(String bootstrapServers, String topic) {
        Map<String, Object> producerProps = new HashMap<>();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class);
        producerProps.put(ProducerConfig.ACKS_CONFIG, "all");
        producerProps.put(ProducerConfig.RETRIES_CONFIG, 3);

        DefaultKafkaProducerFactory<String, byte[]> producerFactory =
                new DefaultKafkaProducerFactory<>(producerProps);
        KafkaTemplate<String, byte[]> kafkaTemplate = new KafkaTemplate<>(producerFactory);

        return new KafkaEventProducer(kafkaTemplate, createEventSerde(), topic);
    }
}