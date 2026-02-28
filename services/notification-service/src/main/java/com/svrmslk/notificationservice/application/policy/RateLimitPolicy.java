// com/svrmslk/notificationservice/application/policy/RateLimitPolicy.java
package com.svrmslk.notificationservice.application.policy;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Component;

@Component
public final class RateLimitPolicy {
    private final Map<String, RateLimitBucket> buckets = new ConcurrentHashMap<>();
    private final int maxRequestsPerWindow;
    private final Duration windowDuration;

    public RateLimitPolicy(int maxRequestsPerWindow, Duration windowDuration) {
        if (maxRequestsPerWindow <= 0) {
            throw new IllegalArgumentException("Max requests per window must be positive");
        }
        if (windowDuration == null || windowDuration.isZero() || windowDuration.isNegative()) {
            throw new IllegalArgumentException("Window duration must be positive");
        }
        this.maxRequestsPerWindow = maxRequestsPerWindow;
        this.windowDuration = windowDuration;
    }

    public boolean allowRequest(String tenantId, String recipient) {
        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalArgumentException("TenantId cannot be null or blank");
        }
        if (recipient == null || recipient.isBlank()) {
            throw new IllegalArgumentException("Recipient cannot be null or blank");
        }

        String key = tenantId + ":" + recipient;
        RateLimitBucket bucket = buckets.compute(key, (k, existing) -> {
            Instant now = Instant.now();
            if (existing == null || existing.isExpired(now)) {
                return new RateLimitBucket(now, windowDuration);
            }
            return existing;
        });

        return bucket.tryConsume(maxRequestsPerWindow);
    }

    private static final class RateLimitBucket {
        private final Instant windowStart;
        private final Duration windowDuration;
        private final AtomicInteger requestCount;

        RateLimitBucket(Instant windowStart, Duration windowDuration) {
            this.windowStart = windowStart;
            this.windowDuration = windowDuration;
            this.requestCount = new AtomicInteger(0);
        }

        boolean isExpired(Instant now) {
            return now.isAfter(windowStart.plus(windowDuration));
        }

        boolean tryConsume(int maxRequests) {
            int current = requestCount.incrementAndGet();
            return current <= maxRequests;
        }
    }
}