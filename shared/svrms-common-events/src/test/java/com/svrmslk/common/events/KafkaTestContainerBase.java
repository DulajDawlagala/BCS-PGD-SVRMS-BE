package com.svrmslk.common.events;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

/**
 * Base class for Kafka integration tests using Testcontainers.
 * Provides shared Kafka container instance for all tests.
 *
 * @author Platform Team
 * @since 1.0.0
 */
@Testcontainers
public abstract class KafkaTestContainerBase {

    protected static final KafkaContainer kafkaContainer;

    static {
        kafkaContainer = new KafkaContainer(
                DockerImageName.parse("confluentinc/cp-kafka:7.5.0")
        )
                .withReuse(true)
                .withEnv("KAFKA_AUTO_CREATE_TOPICS_ENABLE", "true")
                .withEnv("KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR", "1")
                .withEnv("KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR", "1")
                .withEnv("KAFKA_TRANSACTION_STATE_LOG_MIN_ISR", "1");
    }

    @BeforeAll
    static void startContainer() {
        kafkaContainer.start();
    }

    @AfterAll
    static void stopContainer() {
        kafkaContainer.stop();
    }

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
        registry.add("spring.kafka.producer.bootstrap-servers", kafkaContainer::getBootstrapServers);
        registry.add("spring.kafka.consumer.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }

    protected String getBootstrapServers() {
        return kafkaContainer.getBootstrapServers();
    }
}