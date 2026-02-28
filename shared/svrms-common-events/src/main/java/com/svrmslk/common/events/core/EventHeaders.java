package com.svrmslk.common.events.core;

/**
 * Standard header names used in event messaging.
 *
 * @author Platform Team
 * @since 1.0.0
 */
public final class EventHeaders {

    // Identity
    public static final String EVENT_ID = "event-id";
    public static final String EVENT_TYPE = "event-type";
    public static final String EVENT_VERSION = "event-version";
    public static final String EVENT_TIMESTAMP = "event-timestamp";

    // Multi-tenancy
    public static final String TENANT_ID = "tenant-id";

    // Distributed tracing
    public static final String TRACE_ID = "trace-id";
    public static final String CORRELATION_ID = "correlation-id";
    public static final String CAUSATION_ID = "causation-id";

    // Source tracking
    public static final String SOURCE = "source";
    public static final String USER_ID = "user-id";

    // Schema
    public static final String SCHEMA_VERSION = "schema-version";
    public static final String CONTENT_TYPE = "content-type";

    // Security
    public static final String SIGNATURE = "signature";
    public static final String SIGNATURE_ALGORITHM = "signature-algorithm";
    public static final String SIGNED_BY = "signed-by";

    // Retry & DLQ
    public static final String RETRY_COUNT = "retry-count";
    public static final String ORIGINAL_TOPIC = "original-topic";
    public static final String ERROR_MESSAGE = "error-message";
    public static final String ERROR_CAUSE = "error-cause";

    // Multicluster
    public static final String SOURCE_CLUSTER = "source-cluster";
    public static final String TARGET_CLUSTER = "target-cluster";

    private EventHeaders() {
        throw new UnsupportedOperationException("Utility class");
    }
}