package com.svrmslk.common.events.schema.migration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.svrmslk.common.events.core.EventVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for event upcasters that migrate events from old versions to new versions.
 * Enables schema evolution while maintaining backward compatibility.
 *
 * @author Platform Team
 * @since 1.0.0
 */
public abstract class EventUpcaster {

    private static final Logger log = LoggerFactory.getLogger(EventUpcaster.class);

    protected final ObjectMapper objectMapper;
    private final String eventType;
    private final EventVersion fromVersion;
    private final EventVersion toVersion;

    protected EventUpcaster(
            ObjectMapper objectMapper,
            String eventType,
            EventVersion fromVersion,
            EventVersion toVersion) {
        this.objectMapper = objectMapper;
        this.eventType = eventType;
        this.fromVersion = fromVersion;
        this.toVersion = toVersion;

        if (!fromVersion.isCompatibleWith(toVersion)) {
            throw new IllegalArgumentException(
                    String.format("Incompatible version migration: from=%s to=%s", fromVersion, toVersion)
            );
        }
    }

    /**
     * Upcasts an event from the old version to the new version.
     *
     * @param eventData the event data in old format
     * @return the event data in new format
     */
    public abstract JsonNode upcast(JsonNode eventData);

    /**
     * Determines if this upcaster can handle the given event type and version.
     */
    public boolean canUpcast(String eventType, EventVersion version) {
        return this.eventType.equals(eventType) && this.fromVersion.equals(version);
    }

    public String getEventType() {
        return eventType;
    }

    public EventVersion getFromVersion() {
        return fromVersion;
    }

    public EventVersion getToVersion() {
        return toVersion;
    }

    /**
     * Helper method to add a field to the event data.
     */
    protected void addField(ObjectNode node, String fieldName, Object value) {
        if (value instanceof String) {
            node.put(fieldName, (String) value);
        } else if (value instanceof Integer) {
            node.put(fieldName, (Integer) value);
        } else if (value instanceof Long) {
            node.put(fieldName, (Long) value);
        } else if (value instanceof Boolean) {
            node.put(fieldName, (Boolean) value);
        } else {
            node.set(fieldName, objectMapper.valueToTree(value));
        }

        log.debug("Added field during upcast: eventType={}, field={}", eventType, fieldName);
    }

    /**
     * Helper method to remove a field from the event data.
     */
    protected void removeField(ObjectNode node, String fieldName) {
        node.remove(fieldName);
        log.debug("Removed field during upcast: eventType={}, field={}", eventType, fieldName);
    }

    /**
     * Helper method to rename a field.
     */
    protected void renameField(ObjectNode node, String oldName, String newName) {
        if (node.has(oldName)) {
            JsonNode value = node.get(oldName);
            node.set(newName, value);
            node.remove(oldName);
            log.debug("Renamed field during upcast: eventType={}, from={}, to={}",
                    eventType, oldName, newName);
        }
    }
}