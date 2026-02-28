package com.svrmslk.common.events.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Spring configuration for Kafka producers and consumers.
 *
 * @author Platform Team
 * @since 1.0.0
 */
@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${events.kafka.producer.acks:all}")
    private String acks;

    @Value("${events.kafka.producer.retries:3}")
    private int retries;

    @Value("${events.kafka.producer.batch-size:16384}")
    private int batchSize;

    @Value("${events.kafka.producer.linger-ms:10}")
    private int lingerMs;

    @Value("${events.kafka.producer.compression-type:snappy}")
    private String compressionType;

    @Value("${events.kafka.consumer.group-id:event-consumer-group}")
    private String consumerGroupId;

    @Value("${events.kafka.consumer.auto-offset-reset:earliest}")
    private String autoOffsetReset;

    @Value("${events.kafka.consumer.enable-auto-commit:false}")
    private boolean enableAutoCommit;

    @Value("${events.kafka.consumer.max-poll-records:500}")
    private int maxPollRecords;

    @Value("${events.kafka.consumer.concurrency:3}")
    private int consumerConcurrency;

    /**
     * Kafka producer factory configuration.
     */
    @Bean
    public ProducerFactory<String, byte[]> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();

        // Connection
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        // Serialization
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class);

        // Reliability
        configProps.put(ProducerConfig.ACKS_CONFIG, acks);
        configProps.put(ProducerConfig.RETRIES_CONFIG, retries);
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        configProps.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);

        // Performance
        configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, batchSize);
        configProps.put(ProducerConfig.LINGER_MS_CONFIG, lingerMs);
        configProps.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, compressionType);
        configProps.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432); // 32MB

        // Timeouts
        configProps.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000);
        configProps.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 120000);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    /**
     * Kafka template for sending messages.
     */
    @Bean
    public KafkaTemplate<String, byte[]> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    /**
     * Kafka consumer factory configuration.
     */
    @Bean
    public ConsumerFactory<String, byte[]> consumerFactory() {
        Map<String, Object> configProps = new HashMap<>();

        // Connection
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        // Deserialization
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class);

        // Consumer group
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);
        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        configProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);

        // Performance
        configProps.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
        configProps.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, 1);
        configProps.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 500);

        // Reliability
        configProps.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 30000);
        configProps.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 10000);
        configProps.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 300000);

        // Isolation
        configProps.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");

        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    /**
     * Kafka listener container factory with manual acknowledgment.
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, byte[]> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, byte[]> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(consumerConcurrency);

        // Manual acknowledgment for better error handling
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);

        // Error handling
        factory.setCommonErrorHandler(new org.springframework.kafka.listener.DefaultErrorHandler());

        return factory;
    }
}