package com.svrmslk.common.events.replay;

import com.svrmslk.common.events.api.EventStore;
import com.svrmslk.common.events.consumer.EventDispatcher;
import com.svrmslk.common.events.core.EventEnvelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Replays historical events from the event store.
 * Useful for rebuilding read models, testing, or recovering from failures.
 *
 * @author Platform Team
 * @since 1.0.0
 */
public class EventReplayer {

    private static final Logger log = LoggerFactory.getLogger(EventReplayer.class);

    private final EventStore eventStore;
    private final EventDispatcher eventDispatcher;
    private final ExecutorService executorService;

    public EventReplayer(EventStore eventStore, EventDispatcher eventDispatcher) {
        this.eventStore = eventStore;
        this.eventDispatcher = eventDispatcher;
        this.executorService = Executors.newFixedThreadPool(4); // Configurable
    }

    /**
     * Replays all events within a time range.
     *
     * @param start start timestamp (inclusive)
     * @param end end timestamp (exclusive)
     * @param policy replay policy controlling behavior
     * @return future that completes when replay is done
     */
    public CompletableFuture<ReplayResult> replay(Instant start, Instant end, ReplayPolicy policy) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("Starting event replay: start={}, end={}, policy={}", start, end, policy);

            long startTime = System.currentTimeMillis();
            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failureCount = new AtomicInteger(0);

            try {
                // Fetch events from store
                List<EventEnvelope<?>> events = eventStore.findByTimeRange(start, end);

                log.info("Found {} events to replay", events.size());

                for (EventEnvelope<?> envelope : events) {
                    if (policy.shouldReplay(envelope)) {
                        try {
                            eventDispatcher.dispatch(envelope);
                            successCount.incrementAndGet();

                            // Apply rate limiting if specified
                            if (policy.getDelayBetweenEvents() > 0) {
                                Thread.sleep(policy.getDelayBetweenEvents());
                            }

                        } catch (Exception ex) {
                            failureCount.incrementAndGet();
                            log.error("Failed to replay event: eventId={}, eventType={}",
                                    envelope.getEventId(), envelope.getEventType(), ex);

                            if (policy.isStopOnError()) {
                                throw new ReplayException(
                                        "Replay stopped due to error", ex, successCount.get(), failureCount.get());
                            }
                        }
                    }
                }

                long duration = System.currentTimeMillis() - startTime;

                log.info("Event replay completed: success={}, failures={}, duration={}ms",
                        successCount.get(), failureCount.get(), duration);

                return new ReplayResult(
                        successCount.get(),
                        failureCount.get(),
                        duration,
                        true
                );

            } catch (Exception ex) {
                long duration = System.currentTimeMillis() - startTime;

                log.error("Event replay failed: success={}, failures={}, duration={}ms",
                        successCount.get(), failureCount.get(), duration, ex);

                return new ReplayResult(
                        successCount.get(),
                        failureCount.get(),
                        duration,
                        false
                );
            }
        }, executorService);
    }

    /**
     * Replays events of a specific type within a time range.
     */
    public CompletableFuture<ReplayResult> replayByType(
            String eventType, Instant start, Instant end, ReplayPolicy policy) {

        return CompletableFuture.supplyAsync(() -> {
            log.info("Starting event replay by type: eventType={}, start={}, end={}",
                    eventType, start, end);

            long startTime = System.currentTimeMillis();
            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failureCount = new AtomicInteger(0);

            try {
                List<EventEnvelope<?>> events = eventStore.findByTypeAndTimeRange(eventType, start, end);

                log.info("Found {} events of type {} to replay", events.size(), eventType);

                for (EventEnvelope<?> envelope : events) {
                    if (policy.shouldReplay(envelope)) {
                        try {
                            eventDispatcher.dispatch(envelope);
                            successCount.incrementAndGet();

                            if (policy.getDelayBetweenEvents() > 0) {
                                Thread.sleep(policy.getDelayBetweenEvents());
                            }

                        } catch (Exception ex) {
                            failureCount.incrementAndGet();
                            log.error("Failed to replay event: eventId={}",
                                    envelope.getEventId(), ex);

                            if (policy.isStopOnError()) {
                                throw new ReplayException(
                                        "Replay stopped due to error", ex,
                                        successCount.get(), failureCount.get());
                            }
                        }
                    }
                }

                long duration = System.currentTimeMillis() - startTime;

                log.info("Event replay by type completed: eventType={}, success={}, failures={}, duration={}ms",
                        eventType, successCount.get(), failureCount.get(), duration);

                return new ReplayResult(
                        successCount.get(),
                        failureCount.get(),
                        duration,
                        true
                );

            } catch (Exception ex) {
                long duration = System.currentTimeMillis() - startTime;

                log.error("Event replay by type failed: eventType={}, duration={}ms",
                        eventType, duration, ex);

                return new ReplayResult(
                        successCount.get(),
                        failureCount.get(),
                        duration,
                        false
                );
            }
        }, executorService);
    }

    /**
     * Shuts down the replayer and its executor service.
     */
    public void shutdown() {
        log.info("Shutting down event replayer");
        executorService.shutdown();
    }

    /**
     * Result of a replay operation.
     */
    public record ReplayResult(
            int successCount,
            int failureCount,
            long durationMs,
            boolean completed
    ) {}

    /**
     * Exception thrown during replay operations.
     */
    public static class ReplayException extends RuntimeException {
        private final int successCount;
        private final int failureCount;

        public ReplayException(String message, Throwable cause, int successCount, int failureCount) {
            super(message, cause);
            this.successCount = successCount;
            this.failureCount = failureCount;
        }

        public int getSuccessCount() {
            return successCount;
        }

        public int getFailureCount() {
            return failureCount;
        }
    }
}