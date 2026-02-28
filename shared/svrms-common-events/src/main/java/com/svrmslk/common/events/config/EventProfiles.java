package com.svrmslk.common.events.config;

/**
 * Spring profile constants for event platform configuration.
 *
 * @author Platform Team
 * @since 1.0.0
 */
public final class EventProfiles {

    /**
     * Development profile - relaxed validation, verbose logging.
     */
    public static final String DEV = "dev";

    /**
     * Testing profile - in-memory Kafka, test containers.
     */
    public static final String TEST = "test";

    /**
     * Production profile - strict validation, optimized performance.
     */
    public static final String PROD = "prod";

    /**
     * Local profile - local Kafka instance.
     */
    public static final String LOCAL = "local";

    private EventProfiles() {
        throw new UnsupportedOperationException("Utility class");
    }
}