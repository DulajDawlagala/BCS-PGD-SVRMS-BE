// com/svrmslk/notificationservice/config/ObservabilityConfig.java
package com.svrmslk.notificationservice.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObservabilityConfig {

    private static final String METRIC_PREFIX = "notification.service";

    @Bean
    public Counter emailNotificationsSentCounter(MeterRegistry registry) {
        return Counter.builder(METRIC_PREFIX + ".email.sent")
                .description("Total number of email notifications sent successfully")
                .register(registry);
    }

    @Bean
    public Counter emailNotificationsFailedCounter(MeterRegistry registry) {
        return Counter.builder(METRIC_PREFIX + ".email.failed")
                .description("Total number of email notifications that failed")
                .register(registry);
    }

    @Bean
    public Counter inAppNotificationsSentCounter(MeterRegistry registry) {
        return Counter.builder(METRIC_PREFIX + ".inapp.sent")
                .description("Total number of in-app notifications sent successfully")
                .register(registry);
    }

    @Bean
    public Counter inAppNotificationsFailedCounter(MeterRegistry registry) {
        return Counter.builder(METRIC_PREFIX + ".inapp.failed")
                .description("Total number of in-app notifications that failed")
                .register(registry);
    }

    @Bean
    public Counter duplicateEventsCounter(MeterRegistry registry) {
        return Counter.builder(METRIC_PREFIX + ".events.duplicate")
                .description("Total number of duplicate events detected")
                .register(registry);
    }

    @Bean
    public Counter rateLimitExceededCounter(MeterRegistry registry) {
        return Counter.builder(METRIC_PREFIX + ".ratelimit.exceeded")
                .description("Total number of notifications rejected due to rate limiting")
                .register(registry);
    }

    @Bean
    public Counter kafkaMessagesReceivedCounter(MeterRegistry registry) {
        return Counter.builder(METRIC_PREFIX + ".kafka.messages.received")
                .description("Total number of Kafka messages received")
                .register(registry);
    }

    @Bean
    public Counter kafkaMessagesProcessedCounter(MeterRegistry registry) {
        return Counter.builder(METRIC_PREFIX + ".kafka.messages.processed")
                .description("Total number of Kafka messages processed successfully")
                .register(registry);
    }

    @Bean
    public Counter kafkaMessagesFailedCounter(MeterRegistry registry) {
        return Counter.builder(METRIC_PREFIX + ".kafka.messages.failed")
                .description("Total number of Kafka messages that failed processing")
                .register(registry);
    }

    @Bean
    public Timer emailNotificationTimer(MeterRegistry registry) {
        return Timer.builder(METRIC_PREFIX + ".email.duration")
                .description("Time taken to send email notifications")
                .register(registry);
    }

    @Bean
    public Timer inAppNotificationTimer(MeterRegistry registry) {
        return Timer.builder(METRIC_PREFIX + ".inapp.duration")
                .description("Time taken to send in-app notifications")
                .register(registry);
    }

    @Bean
    public Timer kafkaMessageProcessingTimer(MeterRegistry registry) {
        return Timer.builder(METRIC_PREFIX + ".kafka.processing.duration")
                .description("Time taken to process Kafka messages")
                .register(registry);
    }

    @Bean
    public Counter circuitBreakerOpenCounter(MeterRegistry registry) {
        return Counter.builder(METRIC_PREFIX + ".circuitbreaker.open")
                .description("Total number of times circuit breaker opened")
                .register(registry);
    }

    @Bean
    public Counter tenantIsolationViolationsCounter(MeterRegistry registry) {
        return Counter.builder(METRIC_PREFIX + ".security.tenant.violations")
                .description("Total number of tenant isolation violations detected")
                .register(registry);
    }
}