package com.svrmslk.common.events.schema.migration;

import com.fasterxml.jackson.databind.JsonNode;
import com.svrmslk.common.events.core.EventVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry that manages event upcasters and applies them in sequence.
 * Supports chaining multiple upcasters for multi-version migrations.
 *
 * @author Platform Team
 * @since 1.0.0
 **/
public class EventMigrationRegistry {

    private static final Logger log = LoggerFactory.getLogger(EventMigrationRegistry.class);

    private final Map<String, List<EventUpcaster>> upcasterChains;

    public EventMigrationRegistry() {
        this.upcasterChains = new ConcurrentHashMap<>();
    }

    /**
     * Registers an upcaster for a specific event type.
     *
     * @param upcaster the upcaster to register
     */
    public void registerUpcaster(EventUpcaster upcaster) {
        upcasterChains
                .computeIfAbsent(upcaster.getEventType(), k -> new ArrayList<>())
                .add(upcaster);

        // Sort by version to ensure correct ordering
        upcasterChains.get(upcaster.getEventType()).sort(
                (u1, u2) -> u1.getFromVersion().compareTo(u2.getFromVersion())
        );

        log.info("Registered upcaster: eventType={}, from={}, to={}",
                upcaster.getEventType(), upcaster.getFromVersion(), upcaster.getToVersion());
    }

    /**
     * Migrates event data from its current version to the target version.
     * Applies upcasters in sequence if multiple migrations are needed.
     *
     * @param eventType      the event type
     * @param currentVersion the current version
     * @param targetVersion  the target version
     * @param eventData      the event data
     * @return the migrated event data
     */
    public JsonNode migrate(
            String eventType,
            EventVersion currentVersion,
            EventVersion targetVersion,
            JsonNode eventData) {

        if (currentVersion.equals(targetVersion)) {
            log.debug("No migration needed: eventType={}, version={}", eventType, currentVersion);
            return eventData;
        }

        List<EventUpcaster> upcasters = upcasterChains.get(eventType);
        if (upcasters == null || upcasters.isEmpty()) {
            log.warn("No upcasters registered for event type: {}", eventType);
            return eventData;
        }

        log.info("Migrating event: eventType={}, from={}, to={}", eventType, currentVersion, targetVersion);

        JsonNode result = eventData;
        EventVersion version = currentVersion;

        // Apply upcasters in sequence
        for (EventUpcaster upcaster : upcasters) {
            if (upcaster.canUpcast(eventType, version)) {
                log.debug("Applying upcaster: from={}, to={}", version, upcaster.getToVersion());
                result = upcaster.upcast(result);
                version = upcaster.getToVersion();

                if (version.equals(targetVersion)) {
                    break;
                }
            }
        }

        if (!version.equals(targetVersion)) {
            log.warn("Could not fully migrate event to target version: eventType={}, achieved={}, target={}",
                    eventType, version, targetVersion);
        } else {
            log.info("Event migration completed: eventType={}, version={}", eventType, version);
        }

        return result;
    }

    /**
     * Checks if migration is available for an event type.
     */
    public boolean hasMigration(String eventType) {
        List<EventUpcaster> upcasters = upcasterChains.get(eventType);
        return upcasters != null && !upcasters.isEmpty();
    }

    /**
     * Gets the latest version for an event type based on registered upcasters.
     */
    public EventVersion getLatestVersion(String eventType) {
        List<EventUpcaster> upcasters = upcasterChains.get(eventType);
        if (upcasters == null || upcasters.isEmpty()) {
            return EventVersion.V1_0_0;
        }

        return upcasters.stream()
                .map(EventUpcaster::getToVersion)
                .max(EventVersion::compareTo)
                .orElse(EventVersion.V1_0_0);
    }
}