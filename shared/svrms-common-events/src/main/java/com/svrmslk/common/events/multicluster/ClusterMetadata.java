package com.svrmslk.common.events.multicluster;

/**
 * Metadata about a Kafka cluster configuration.
 * Used for multi-cluster routing and failover scenarios.
 *
 * @author Platform Team
 * @since 1.0.0
 */
public record ClusterMetadata(
        String clusterId,
        String bootstrapServers,
        String region,
        boolean isPrimary,
        int priority
) {

    public ClusterMetadata {
        if (clusterId == null || clusterId.isBlank()) {
            throw new IllegalArgumentException("clusterId cannot be null or blank");
        }
        if (bootstrapServers == null || bootstrapServers.isBlank()) {
            throw new IllegalArgumentException("bootstrapServers cannot be null or blank");
        }
        if (priority < 0) {
            throw new IllegalArgumentException("priority must be non-negative");
        }
    }

    /**
     * Creates primary cluster metadata.
     */
    public static ClusterMetadata primary(String clusterId, String bootstrapServers, String region) {
        return new ClusterMetadata(clusterId, bootstrapServers, region, true, 0);
    }

    /**
     * Creates secondary/backup cluster metadata.
     */
    public static ClusterMetadata secondary(String clusterId, String bootstrapServers, String region, int priority) {
        return new ClusterMetadata(clusterId, bootstrapServers, region, false, priority);
    }
}