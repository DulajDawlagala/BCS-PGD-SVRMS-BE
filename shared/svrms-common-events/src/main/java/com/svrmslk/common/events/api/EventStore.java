package com.svrmslk.common.events.api;

import com.svrmslk.common.events.core.EventEnvelope;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Abstraction for querying and replaying historical events.
 * This is optional - useful for Event Sourcing or audit requirements.
 *
 * @author Platform Team
 * @since 1.0.0
 */
public interface EventStore {

    /**
     * Retrieves an event by its unique identifier.
     *
     * @param eventId the event identifier
     * @return the event envelope if found
     */
    Optional<EventEnvelope<?>> findById(String eventId);

    /**
     * Retrieves events for a specific aggregate/entity.
     *
     * @param aggregateId the aggregate identifier
     * @return list of events in order
     */
    List<EventEnvelope<?>> findByAggregateId(String aggregateId);

    /**
     * Retrieves events within a time range.
     *
     * @param start start timestamp (inclusive)
     * @param end end timestamp (exclusive)
     * @return list of events in chronological order
     */
    List<EventEnvelope<?>> findByTimeRange(Instant start, Instant end);

    /**
     * Retrieves events of a specific type within a time range.
     *
     * @param eventType the event type to filter
     * @param start start timestamp
     * @param end end timestamp
     * @return list of matching events
     */
    List<EventEnvelope<?>> findByTypeAndTimeRange(String eventType, Instant start, Instant end);
}