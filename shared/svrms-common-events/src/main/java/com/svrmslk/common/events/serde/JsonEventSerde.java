package com.svrmslk.common.events.serde;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.svrmslk.common.events.api.Event;
import com.svrmslk.common.events.core.EventEnvelope;
import com.svrmslk.common.events.core.EventMetadata;
import com.svrmslk.common.events.core.EventType;
import com.svrmslk.common.events.core.EventVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.svrmslk.common.events.domain.notification.EmailNotificationRequestedEvent;
import com.svrmslk.common.events.domain.notification.InAppNotificationRequestedEvent;

/**
 * JSON-based implementation of EventSerializer and EventDeserializer.
 * Uses Jackson for high-performance JSON processing.
 *
 * @author Platform Team
 * @since 1.0.0
 */
public class JsonEventSerde implements EventSerializer, EventDeserializer {

    private static final Logger log = LoggerFactory.getLogger(JsonEventSerde.class);

    private final ObjectMapper objectMapper;
    private final Map<String, Class<? extends Event>> eventTypeRegistry;

    public JsonEventSerde() {
        this.objectMapper = createObjectMapper();
        this.eventTypeRegistry = new ConcurrentHashMap<>();
    }

    public JsonEventSerde(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.eventTypeRegistry = new ConcurrentHashMap<>();
    }

    /**
     * Registers an event class for deserialization.
     *
     * @param eventClass the event class
     */
    public void registerEventType(Class<? extends Event> eventClass) {
        EventType eventType = EventType.fromClass(eventClass);
        eventTypeRegistry.put(eventType.value(), eventClass);
        log.debug("Registered event type: {} -> {}", eventType.value(), eventClass.getName());
    }

    /**
     * Auto-register common event types on initialization.
     */
    public void registerCommonEventTypes() {
        // Auth domain
        registerEventType(com.svrmslk.common.events.domain.auth.UserRegisteredEvent.class);

        // Notification domain
        registerEventType(EmailNotificationRequestedEvent.class);
        registerEventType(InAppNotificationRequestedEvent.class);

        log.info("Registered common event types");
    }

    @Override
    public byte[] serialize(EventEnvelope<?> envelope) {
        try {
            // Create JSON structure: { metadata: {...}, payload: {...} }
            Map<String, Object> structure = Map.of(
                    "metadata", envelope.metadata(),
                    "payload", envelope.payload()
            );

            byte[] bytes = objectMapper.writeValueAsBytes(structure);

            log.debug("Serialized event: eventId={}, size={}bytes",
                    envelope.getEventId(), bytes.length);

            return bytes;

        } catch (JsonProcessingException ex) {
            log.error("Failed to serialize event: eventId={}, eventType={}",
                    envelope.getEventId(), envelope.getEventType(), ex);
            throw new SerializationException("JSON serialization failed", ex);
        }
    }

    @Override
    public EventEnvelope<?> deserialize(byte[] data, String eventType) {
        try {
            // Parse JSON tree
            JsonNode root = objectMapper.readTree(data);
            JsonNode metadataNode = root.get("metadata");
            JsonNode payloadNode = root.get("payload");

            if (metadataNode == null || payloadNode == null) {
                throw new DeserializationException(
                        "Invalid event structure: missing metadata or payload", null);
            }

            // Deserialize metadata
            EventMetadata metadata = deserializeMetadata(metadataNode);

            // Deserialize payload based on registered type
            Class<? extends Event> eventClass = eventTypeRegistry.get(eventType);
            if (eventClass == null) {
                log.warn("Event type not registered, using generic Event: {}", eventType);
                throw new DeserializationException(
                        "Event type not registered: " + eventType, null);
            }

            Event payload = objectMapper.treeToValue(payloadNode, eventClass);

            log.debug("Deserialized event: eventId={}, type={}",
                    metadata.eventId(), eventType);

            return new EventEnvelope<>(metadata, payload);

        } catch (IOException ex) {
            log.error("Failed to deserialize event: eventType={}", eventType, ex);
            throw new DeserializationException("JSON deserialization failed", ex);
        }
    }

    /**
     * Deserializes metadata from JSON node.
     */
    private EventMetadata deserializeMetadata(JsonNode node) throws IOException {
        String eventId = node.get("eventId").asText();
        String eventTypeValue = node.get("eventType").get("value").asText();
        EventType eventType = EventType.of(eventTypeValue);

        JsonNode versionNode = node.get("version");
        EventVersion version = new EventVersion(
                versionNode.get("major").asInt(),
                versionNode.get("minor").asInt(),
                versionNode.get("patch").asInt()
        );

        Instant timestamp = Instant.parse(node.get("timestamp").asText());

        String tenantId = node.has("tenantId") && !node.get("tenantId").isNull()
                ? node.get("tenantId").asText() : null;
        String traceId = node.has("traceId") && !node.get("traceId").isNull()
                ? node.get("traceId").asText() : null;
        String correlationId = node.has("correlationId") && !node.get("correlationId").isNull()
                ? node.get("correlationId").asText() : null;
        String causationId = node.has("causationId") && !node.get("causationId").isNull()
                ? node.get("causationId").asText() : null;
        String userId = node.has("userId") && !node.get("userId").isNull()
                ? node.get("userId").asText() : null;
        String source = node.has("source") && !node.get("source").isNull()
                ? node.get("source").asText() : null;

        Map<String, String> customHeaders = Map.of(); // Simplified for now

        return new EventMetadata(
                eventId, eventType, version, timestamp, tenantId,
                traceId, correlationId, causationId, userId, source, customHeaders
        );
    }

    /**
     * Creates a configured ObjectMapper for event serialization.
     */
    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Register Java 8 time module
        mapper.registerModule(new JavaTimeModule());

        // Don't write dates as timestamps
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Don't fail on unknown properties
        mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return mapper;
    }
}