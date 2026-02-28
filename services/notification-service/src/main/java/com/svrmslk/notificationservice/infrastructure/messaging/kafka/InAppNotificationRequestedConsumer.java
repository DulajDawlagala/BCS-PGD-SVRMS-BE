// com/svrmslk/notificationservice/infrastructure/messaging/kafka/InAppNotificationRequestedConsumer.java

//package com.svrmslk.notificationservice.infrastructure.messaging.kafka;
//
//import com.svrmslk.common.events.domain.notification.InAppNotificationRequestedEvent;
//import com.svrmslk.notificationservice.application.policy.RateLimitExceededException;
//import com.svrmslk.notificationservice.application.port.in.InAppNotificationCommand;
//import com.svrmslk.notificationservice.application.usecase.InAppNotificationFailedException;
//import com.svrmslk.notificationservice.application.usecase.SendInAppNotificationUseCase;
//import com.svrmslk.notificationservice.common.context.ContextHolder;
//import com.svrmslk.notificationservice.common.context.CorrelationContext;
//import com.svrmslk.notificationservice.common.context.TenantContext;
//import com.svrmslk.notificationservice.common.logging.MdcContextInitializer;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.kafka.support.Acknowledgment;
//import org.springframework.kafka.support.KafkaHeaders;
//import org.springframework.messaging.handler.annotation.Header;
//import org.springframework.messaging.handler.annotation.Payload;
//import org.springframework.stereotype.Component;
//
//import java.util.UUID;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//
//@Component
//public class InAppNotificationRequestedConsumer {
//
//    private static final Logger logger =
//            LoggerFactory.getLogger(InAppNotificationRequestedConsumer.class);
//
//    private static final String TENANT_ID_HEADER = "tenantId";
//    private static final String CORRELATION_ID_HEADER = "correlationId";
//
//    private final SendInAppNotificationUseCase sendInAppNotificationUseCase;
//
//    private Map<String, String> toStringMetadata(Map<String, Object> metadata) {
//        if (metadata == null || metadata.isEmpty()) {
//            return Map.of();
//        }
//
//        return metadata.entrySet().stream()
//                .collect(Collectors.toMap(
//                        Map.Entry::getKey,
//                        e -> String.valueOf(e.getValue())
//                ));
//    }
//
//    public InAppNotificationRequestedConsumer(
//            SendInAppNotificationUseCase sendInAppNotificationUseCase
//    ) {
//        this.sendInAppNotificationUseCase = sendInAppNotificationUseCase;
//    }
//
//    @KafkaListener(
//            topics = "${kafka.topics.inapp-notification-requested}",
//            groupId = "${spring.kafka.consumer.group-id}",
//            containerFactory = "kafkaListenerContainerFactory"
//    )
//    public void consume(
//            @Payload InAppNotificationRequestedEvent event,
//            @Header(value = TENANT_ID_HEADER, required = false) String tenantIdHeader,
//            @Header(value = CORRELATION_ID_HEADER, required = false) String correlationIdHeader,
//            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
//            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
//            @Header(KafkaHeaders.OFFSET) long offset,
//            Acknowledgment acknowledgment
//    ) {
//        String tenantId = extractTenantId(event, tenantIdHeader);
//        String correlationId = extractCorrelationId(correlationIdHeader);
//
//        try {
//            initializeContext(tenantId, correlationId);
//
//            logger.info(
//                    "Received InAppNotificationRequestedEvent from topic: {}, partition: {}, offset: {}, eventId: {}",
//                    topic, partition, offset, event.getEventId()
//            );
//
//            InAppNotificationCommand command = new InAppNotificationCommand(
//                    tenantId,
//                    event.getEventId(),
//                    event.recipient(),
//                    event.content(),
//                    toStringMetadata(event.metadata())
//            );
//
//            sendInAppNotificationUseCase.execute(command);
//
//            acknowledgment.acknowledge();
//            logger.info(
//                    "Successfully processed InAppNotificationRequestedEvent, eventId: {}",
//                    event.getEventId()
//            );
//
//        } catch (RateLimitExceededException e) {
//            logger.warn(
//                    "Rate limit exceeded for eventId: {}, tenant: {}, recipient: {}. Acknowledging to skip.",
//                    event.getEventId(), e.getTenantId(), e.getRecipient()
//            );
//            acknowledgment.acknowledge();
//
//        } catch (InAppNotificationFailedException e) {
//            logger.error(
//                    "Failed to send in-app notification for eventId: {}. Will NOT acknowledge.",
//                    event.getEventId(), e
//            );
//
//        } catch (Exception e) {
//            logger.error(
//                    "Unexpected error processing InAppNotificationRequestedEvent, eventId: {}. Acknowledging.",
//                    event.getEventId(), e
//            );
//            acknowledgment.acknowledge();
//
//        } finally {
//            cleanupContext();
//        }
//    }
//
//    private String extractTenantId(
//            InAppNotificationRequestedEvent event,
//            String tenantIdHeader
//    ) {
//        String tenantId =
//                tenantIdHeader != null ? tenantIdHeader : event.getTenantId();
//
//        if (tenantId == null || tenantId.isBlank()) {
//            throw new IllegalArgumentException("TenantId not found in event or headers");
//        }
//        return tenantId;
//    }
//
//    private String extractCorrelationId(String correlationIdHeader) {
//        return (correlationIdHeader != null && !correlationIdHeader.isBlank())
//                ? correlationIdHeader
//                : UUID.randomUUID().toString();
//    }
//
//    private void initializeContext(String tenantId, String correlationId) {
//        ContextHolder.setTenantContext(new TenantContext(tenantId));
//        ContextHolder.setCorrelationContext(new CorrelationContext(correlationId));
//        MdcContextInitializer.initialize(tenantId, correlationId);
//    }
//
//    private void cleanupContext() {
//        ContextHolder.clearAll();
//        MdcContextInitializer.clear();
//    }
//}

// com/svrmslk/notificationservice/infrastructure/messaging/kafka/InAppNotificationRequestedConsumer.java
package com.svrmslk.notificationservice.infrastructure.messaging.kafka;

import com.svrmslk.common.events.domain.notification.InAppNotificationRequestedEvent;
import com.svrmslk.notificationservice.application.port.in.InAppNotificationCommand;
import com.svrmslk.notificationservice.application.usecase.InAppNotificationFailedException;
import com.svrmslk.notificationservice.application.usecase.SendInAppNotificationUseCase;
import com.svrmslk.notificationservice.common.context.ContextHolder;
import com.svrmslk.notificationservice.common.context.CorrelationContext;
import com.svrmslk.notificationservice.common.context.TenantContext;
import com.svrmslk.notificationservice.common.logging.MdcContextInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class InAppNotificationRequestedConsumer {

    private static final Logger logger =
            LoggerFactory.getLogger(InAppNotificationRequestedConsumer.class);

    private static final String TENANT_ID_HEADER = "tenantId";
    private static final String CORRELATION_ID_HEADER = "correlationId";

    private final SendInAppNotificationUseCase sendInAppNotificationUseCase;

    public InAppNotificationRequestedConsumer(
            SendInAppNotificationUseCase sendInAppNotificationUseCase
    ) {
        this.sendInAppNotificationUseCase = sendInAppNotificationUseCase;
    }

    @KafkaListener(
            topics = "${kafka.topics.inapp-notification-requested}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(
            @Payload InAppNotificationRequestedEvent event,
            @Header(value = TENANT_ID_HEADER, required = false) String tenantIdHeader,
            @Header(value = CORRELATION_ID_HEADER, required = false) String correlationIdHeader,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment
    ) {
        String tenantId = extractTenantId(event, tenantIdHeader);
        String correlationId = extractCorrelationId(correlationIdHeader);

        try {
            initializeContext(tenantId, correlationId);

            logger.info(
                    "Received InAppNotificationRequestedEvent from topic: {}, partition: {}, offset: {}, eventId: {}",
                    topic, partition, offset, event.getEventId()
            );

            // Build notification content combining title and message
            String content = buildNotificationContent(event);

            InAppNotificationCommand command = new InAppNotificationCommand(
                    tenantId,
                    event.getEventId(),
                    event.userId(),
                    content,
                    event.metadata()
            );

            sendInAppNotificationUseCase.execute(command);

            acknowledgment.acknowledge();
            logger.info(
                    "Successfully processed InAppNotificationRequestedEvent, eventId: {}",
                    event.getEventId()
            );

        } catch (InAppNotificationFailedException e) {
            logger.error(
                    "Failed to send in-app notification for eventId: {}. Will NOT acknowledge.",
                    event.getEventId(), e
            );

        } catch (Exception e) {
            logger.error(
                    "Unexpected error processing InAppNotificationRequestedEvent, eventId: {}. Acknowledging.",
                    event.getEventId(), e
            );
            acknowledgment.acknowledge();

        } finally {
            cleanupContext();
        }
    }

    private String buildNotificationContent(InAppNotificationRequestedEvent event) {
        // Combine title and message into content
        StringBuilder content = new StringBuilder();
        content.append(event.title());
        if (event.message() != null && !event.message().isBlank()) {
            content.append(": ").append(event.message());
        }
        return content.toString();
    }

    private String extractTenantId(
            InAppNotificationRequestedEvent event,
            String tenantIdHeader
    ) {
        String tenantId =
                tenantIdHeader != null ? tenantIdHeader : event.getTenantId();

        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalArgumentException("TenantId not found in event or headers");
        }
        return tenantId;
    }

    private String extractCorrelationId(String correlationIdHeader) {
        return (correlationIdHeader != null && !correlationIdHeader.isBlank())
                ? correlationIdHeader
                : UUID.randomUUID().toString();
    }

    private void initializeContext(String tenantId, String correlationId) {
        ContextHolder.setTenantContext(new TenantContext(tenantId));
        ContextHolder.setCorrelationContext(new CorrelationContext(correlationId));
        MdcContextInitializer.initialize(tenantId, correlationId);
    }

    private void cleanupContext() {
        ContextHolder.clearAll();
        MdcContextInitializer.clear();
    }
}
