package com.svrmslk.common.events.schema.validation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import com.svrmslk.common.events.api.Event;
import com.svrmslk.common.events.core.EventType;
import com.svrmslk.common.events.exception.SchemaValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Validates events against JSON schemas.
 * Supports schema registration, caching, and validation with detailed error messages.
 *
 * @author Platform Team
 * @since 1.0.0
 */
public class JsonSchemaValidator {

    private static final Logger log = LoggerFactory.getLogger(JsonSchemaValidator.class);

    private final ObjectMapper objectMapper;
    private final JsonSchemaFactory schemaFactory;
    private final Map<String, JsonSchema> schemaCache;
    private final boolean strictValidation;

    public JsonSchemaValidator(ObjectMapper objectMapper, boolean strictValidation) {
        this.objectMapper = objectMapper;
        this.schemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
        this.schemaCache = new ConcurrentHashMap<>();
        this.strictValidation = strictValidation;

        log.info("JsonSchemaValidator initialized: strictValidation={}", strictValidation);
    }

    public JsonSchemaValidator(ObjectMapper objectMapper) {
        this(objectMapper, true);
    }

    /**
     * Validates an event against its registered schema.
     *
     * @param event the event to validate
     * @throws SchemaValidationException if validation fails
     */
    public void validate(Event event) {
        long startTime = System.currentTimeMillis();
        EventType eventType = EventType.fromClass(event.getClass());

        try {
            JsonSchema schema = getSchema(eventType.value());

            if (schema == null) {
                if (strictValidation) {
                    throw new SchemaValidationException(
                            eventType.value(),
                            "No schema registered for event type"
                    );
                } else {
                    log.warn("No schema found for event type, skipping validation: {}", eventType.value());
                    return;
                }
            }

            // Convert event to JsonNode
            JsonNode eventNode = objectMapper.valueToTree(event);

            // Validate against schema
            Set<ValidationMessage> validationMessages = schema.validate(eventNode);

            if (!validationMessages.isEmpty()) {
                List<String> errors = validationMessages.stream()
                        .map(ValidationMessage::getMessage)
                        .collect(Collectors.toList());

                long duration = System.currentTimeMillis() - startTime;
                log.error("Schema validation failed for event: eventType={}, errors={}, duration={}ms",
                        eventType.value(), errors, duration);

                throw new SchemaValidationException(eventType.value(), errors);
            }

            long duration = System.currentTimeMillis() - startTime;
            log.debug("Schema validation passed: eventType={}, duration={}ms", eventType.value(), duration);

        } catch (SchemaValidationException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error during schema validation: eventType={}", eventType.value(), ex);
            throw new SchemaValidationException(
                    eventType.value(),
                    "Schema validation error: " + ex.getMessage()
            );
        }
    }

    /**
     * Registers a schema from a JSON string.
     *
     * @param eventType the event type
     * @param schemaJson the JSON schema as string
     */
    public void registerSchema(String eventType, String schemaJson) {
        try {
            JsonSchema schema = schemaFactory.getSchema(schemaJson);
            schemaCache.put(eventType, schema);

            log.info("Registered schema for event type: {}", eventType);

        } catch (Exception ex) {
            log.error("Failed to register schema for event type: {}", eventType, ex);
            throw new IllegalArgumentException("Invalid JSON schema for event type: " + eventType, ex);
        }
    }

    /**
     * Registers a schema from an InputStream.
     *
     * @param eventType the event type
     * @param schemaStream the schema input stream
     */
    public void registerSchema(String eventType, InputStream schemaStream) {
        try {
            JsonSchema schema = schemaFactory.getSchema(schemaStream);
            schemaCache.put(eventType, schema);

            log.info("Registered schema for event type from stream: {}", eventType);

        } catch (Exception ex) {
            log.error("Failed to register schema from stream for event type: {}", eventType, ex);
            throw new IllegalArgumentException("Invalid JSON schema stream for event type: " + eventType, ex);
        }
    }

    /**
     * Registers a schema from a JsonNode.
     *
     * @param eventType the event type
     * @param schemaNode the schema JSON node
     */
    public void registerSchema(String eventType, JsonNode schemaNode) {
        try {
            JsonSchema schema = schemaFactory.getSchema(schemaNode);
            schemaCache.put(eventType, schema);

            log.info("Registered schema for event type from JsonNode: {}", eventType);

        } catch (Exception ex) {
            log.error("Failed to register schema from JsonNode for event type: {}", eventType, ex);
            throw new IllegalArgumentException("Invalid JSON schema node for event type: " + eventType, ex);
        }
    }

    /**
     * Removes a schema from the cache.
     *
     * @param eventType the event type
     */
    public void unregisterSchema(String eventType) {
        JsonSchema removed = schemaCache.remove(eventType);
        if (removed != null) {
            log.info("Unregistered schema for event type: {}", eventType);
        } else {
            log.warn("No schema found to unregister for event type: {}", eventType);
        }
    }

    /**
     * Gets a schema from the cache.
     */
    private JsonSchema getSchema(String eventType) {
        return schemaCache.get(eventType);
    }

    /**
     * Checks if a schema is registered for an event type.
     */
    public boolean hasSchema(String eventType) {
        return schemaCache.containsKey(eventType);
    }

    /**
     * Gets all registered event types.
     */
    public Set<String> getRegisteredEventTypes() {
        return schemaCache.keySet();
    }

    /**
     * Clears all cached schemas.
     */
    public void clearCache() {
        schemaCache.clear();
        log.info("Cleared all cached schemas");
    }
}