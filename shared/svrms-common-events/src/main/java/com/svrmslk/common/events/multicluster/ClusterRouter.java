package com.svrmslk.common.events.multicluster;

import com.svrmslk.common.events.core.EventEnvelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Routes events to appropriate Kafka clusters based on rules.
 * Supports multi-region deployments and failover scenarios.
 *
 * @author Platform Team
 * @since 1.0.0
 */
public class ClusterRouter {

    private static final Logger log = LoggerFactory.getLogger(ClusterRouter.class);

    private final List<ClusterMetadata> clusters;
    private final RoutingStrategy routingStrategy;

    public ClusterRouter(List<ClusterMetadata> clusters, RoutingStrategy routingStrategy) {
        if (clusters == null || clusters.isEmpty()) {
            throw new IllegalArgumentException("At least one cluster must be configured");
        }
        this.clusters = new ArrayList<>(clusters);
        this.clusters.sort(Comparator.comparingInt(ClusterMetadata::priority));
        this.routingStrategy = routingStrategy;

        log.info("ClusterRouter initialized with {} clusters, strategy={}",
                clusters.size(), routingStrategy);
    }

    /**
     * Routes an event to the appropriate cluster.
     *
     * @param envelope the event envelope
     * @return the cluster to send the event to
     */
    public ClusterMetadata route(EventEnvelope<?> envelope) {
        return routingStrategy.selectCluster(envelope, clusters);
    }

    /**
     * Gets the primary cluster.
     */
    public Optional<ClusterMetadata> getPrimaryCluster() {
        return clusters.stream()
                .filter(ClusterMetadata::isPrimary)
                .findFirst();
    }

    /**
     * Gets backup clusters sorted by priority.
     */
    public List<ClusterMetadata> getBackupClusters() {
        return clusters.stream()
                .filter(c -> !c.isPrimary())
                .sorted(Comparator.comparingInt(ClusterMetadata::priority))
                .toList();
    }

    /**
     * Strategy interface for cluster selection.
     */
    @FunctionalInterface
    public interface RoutingStrategy {
        ClusterMetadata selectCluster(EventEnvelope<?> envelope, List<ClusterMetadata> clusters);
    }

    /**
     * Always routes to primary cluster.
     */
    public static class PrimaryOnlyStrategy implements RoutingStrategy {
        @Override
        public ClusterMetadata selectCluster(EventEnvelope<?> envelope, List<ClusterMetadata> clusters) {
            return clusters.stream()
                    .filter(ClusterMetadata::isPrimary)
                    .findFirst()
                    .orElse(clusters.get(0));
        }
    }

    /**
     * Routes based on tenant region affinity.
     */
    public static class RegionAffinityStrategy implements RoutingStrategy {
        private final String defaultRegion;

        public RegionAffinityStrategy(String defaultRegion) {
            this.defaultRegion = defaultRegion;
        }

        @Override
        public ClusterMetadata selectCluster(EventEnvelope<?> envelope, List<ClusterMetadata> clusters) {
            // Extract region hint from tenant ID or custom headers
            String targetRegion = extractRegion(envelope);

            // Find cluster in target region
            Optional<ClusterMetadata> regionalCluster = clusters.stream()
                    .filter(c -> c.region().equals(targetRegion))
                    .findFirst();

            if (regionalCluster.isPresent()) {
                return regionalCluster.get();
            }

            // Fallback to default region
            return clusters.stream()
                    .filter(c -> c.region().equals(defaultRegion))
                    .findFirst()
                    .orElse(clusters.get(0));
        }

        private String extractRegion(EventEnvelope<?> envelope) {
            // Simple implementation - could be enhanced with mapping table
            String tenantId = envelope.getTenantId();
            if (tenantId != null && tenantId.contains("-")) {
                String[] parts = tenantId.split("-");
                if (parts.length > 1) {
                    return parts[0]; // e.g., "us-tenant-123" -> "us"
                }
            }
            return defaultRegion;
        }
    }
}