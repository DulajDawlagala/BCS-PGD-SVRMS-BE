package com.svrmslk.common.events.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svrmslk.common.events.api.EventBus;
import com.svrmslk.common.events.api.EventProducer;
import com.svrmslk.common.events.domain.auth.UserRegisteredEvent;
import com.svrmslk.common.events.domain.notification.EmailNotificationRequestedEvent;
import com.svrmslk.common.events.domain.notification.InAppNotificationRequestedEvent;
import com.svrmslk.common.events.observability.EventMetrics;
import com.svrmslk.common.events.observability.EventTracing;
import com.svrmslk.common.events.observability.MetricsInterceptor;
import com.svrmslk.common.events.producer.CircuitBreakerProducer;
import com.svrmslk.common.events.producer.DefaultEventBus;
import com.svrmslk.common.events.producer.KafkaEventProducer;
import com.svrmslk.common.events.producer.RetryableEventProducer;
import com.svrmslk.common.events.schema.validation.JsonSchemaValidator;
import com.svrmslk.common.events.security.EventSigner;
import com.svrmslk.common.events.security.EventVerifier;
import com.svrmslk.common.events.security.HmacEventSigner;
import com.svrmslk.common.events.serde.JsonEventSerde;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.tracing.Tracer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * Spring configuration for event producers and event bus.
 *
 * @author Platform Team
 * @since 1.0.0
 */
@Configuration
public class ProducerConfig {

    @Value("${events.kafka.topics.default:events}")
    private String defaultTopic;

    @Value("${spring.application.name:unknown-app}")
    private String applicationName;

    @Value("${events.security.secret-key:#{null}}")
    private String securitySecretKey;

    @Value("${events.schema.validation.strict:true}")
    private boolean strictSchemaValidation;

    /**
     * JSON event serializer/deserializer with auto-registration.
     */
    @Bean
    @ConditionalOnMissingBean
    public JsonEventSerde jsonEventSerde(ObjectMapper objectMapper) {
        JsonEventSerde serde = new JsonEventSerde(objectMapper);

        // Auto-register domain events
        serde.registerEventType(UserRegisteredEvent.class);
        serde.registerEventType(EmailNotificationRequestedEvent.class);
        serde.registerEventType(InAppNotificationRequestedEvent.class);

        return serde;
    }

    /**
     * Base Kafka event producer.
     */
    @Bean
    public KafkaEventProducer kafkaEventProducer(
            KafkaTemplate<String, byte[]> kafkaTemplate,
            JsonEventSerde eventSerde) {
        return new KafkaEventProducer(kafkaTemplate, eventSerde, defaultTopic);
    }

    /**
     * Retryable event producer decorator.
     */
    @Bean
    public RetryableEventProducer retryableEventProducer(KafkaEventProducer kafkaEventProducer) {
        return RetryableEventProducer.withDefaults(kafkaEventProducer);
    }

    /**
     * Circuit breaker event producer decorator.
     */
    @Bean
    public CircuitBreakerProducer circuitBreakerProducer(RetryableEventProducer retryableEventProducer) {
        return CircuitBreakerProducer.withDefaults(retryableEventProducer, "event-producer-cb");
    }

    /**
     * Metrics interceptor for event producer.
     */
    @Bean
    public EventProducer eventProducer(
            CircuitBreakerProducer circuitBreakerProducer,
            EventMetrics eventMetrics) {
        return new MetricsInterceptor(circuitBreakerProducer, eventMetrics);
    }

    /**
     * Event metrics collector.
     */
    @Bean
    @ConditionalOnMissingBean
    public EventMetrics eventMetrics(MeterRegistry meterRegistry) {
        return new EventMetrics(meterRegistry);
    }

    /**
     * Event tracing support.
     */
    @Bean
    @ConditionalOnMissingBean
    public EventTracing eventTracing(Tracer tracer) {
        return new EventTracing(tracer);
    }

    /**
     * JSON schema validator.
     */
    @Bean
    @ConditionalOnMissingBean
    public JsonSchemaValidator jsonSchemaValidator(ObjectMapper objectMapper) {
        return new JsonSchemaValidator(objectMapper, strictSchemaValidation);
    }

    /**
     * Event signer and verifier.
     */
    @Bean
    @ConditionalOnMissingBean
    public HmacEventSigner hmacEventSigner(ObjectMapper objectMapper) {
        String secretKey = securitySecretKey != null ? securitySecretKey : generateDefaultSecretKey();
        return new HmacEventSigner(secretKey, objectMapper, applicationName);
    }

    @Bean
    @ConditionalOnMissingBean
    public EventSigner eventSigner(HmacEventSigner hmacEventSigner) {
        return hmacEventSigner;
    }

    @Bean
    @ConditionalOnMissingBean
    public EventVerifier eventVerifier(HmacEventSigner hmacEventSigner) {
        return hmacEventSigner;
    }

    /**
     * Event bus - main entry point for publishing events.
     */
    @Bean
    public EventBus eventBus(
            EventProducer eventProducer,
            JsonSchemaValidator schemaValidator,
            EventSigner eventSigner,
            EventMetrics eventMetrics,
            EventTracing eventTracing) {
        return new DefaultEventBus(
                eventProducer,
                schemaValidator,
                eventSigner,
                eventMetrics,
                eventTracing,
                applicationName
        );
    }

    /**
     * Generates a default secret key if none is configured.
     * WARNING: This should only be used for development. Production MUST provide a real secret.
     */
    private String generateDefaultSecretKey() {
        String defaultKey = "default-insecure-key-change-in-production-" + applicationName;
        if (defaultKey.length() < 32) {
            defaultKey = defaultKey + "0".repeat(32 - defaultKey.length());
        }
        return defaultKey;
    }
}