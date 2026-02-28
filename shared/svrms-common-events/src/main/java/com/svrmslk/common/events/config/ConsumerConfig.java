package com.svrmslk.common.events.config;

import com.svrmslk.common.events.consumer.DeadLetterHandler;
import com.svrmslk.common.events.consumer.EventDispatcher;
import com.svrmslk.common.events.consumer.KafkaEventListener;
import com.svrmslk.common.events.consumer.ReplayableConsumer;
import com.svrmslk.common.events.observability.EventMetrics;
import com.svrmslk.common.events.observability.EventTracing;
import com.svrmslk.common.events.security.EventVerifier;
import com.svrmslk.common.events.serde.JsonEventSerde;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * Spring configuration for event consumers.
 *
 * @author Platform Team@since 1.0.0
 * */

 @Configuration


public class ConsumerConfig {

    @Value("${events.kafka.topics.dead-letter:events-dlq}")
    private String deadLetterTopic;
 /**
  *
  * Event dispatcher for routing events to handlers.
  * */

    @Bean
    @ConditionalOnMissingBean

    public EventDispatcher eventDispatcher() {
 return new EventDispatcher();
 }
         /**
  *
  * Dead letter handler for failed events.
  * */
    @Bean
    @ConditionalOnMissingBean

    public DeadLetterHandler deadLetterHandler(KafkaTemplate<String, byte[]> kafkaTemplate) {
 return new DeadLetterHandler(kafkaTemplate, deadLetterTopic);
 }

          /**
  *
  * Replayable consumer for offset management.
  * */

    @Bean
    @ConditionalOnMissingBean

    public ReplayableConsumer replayableConsumer() {
 return new ReplayableConsumer();
 }

          /**
  *
  * Kafka event listener - main consumer component.
  * */

    @Bean

    public KafkaEventListener kafkaEventListener(
 JsonEventSerde eventSerde,
 EventDispatcher eventDispatcher,
 EventVerifier eventVerifier,
 EventMetrics eventMetrics,
 EventTracing eventTracing,
 DeadLetterHandler deadLetterHandler) {
 return new KafkaEventListener(
                 eventSerde,
 eventDispatcher,
 eventVerifier,
 eventMetrics,
 eventTracing,
 deadLetterHandler
                 );
 }

}