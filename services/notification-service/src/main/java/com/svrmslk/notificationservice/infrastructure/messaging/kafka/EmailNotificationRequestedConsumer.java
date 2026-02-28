// com/svrmslk/notificationservice/infrastructure/messaging/kafka/EmailNotificationRequestedConsumer.java
package com.svrmslk.notificationservice.infrastructure.messaging.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svrmslk.common.events.domain.notification.EmailNotificationRequestedEvent;
import com.svrmslk.notificationservice.application.port.in.EmailNotificationCommand;
import com.svrmslk.notificationservice.application.usecase.EmailNotificationFailedException;
import com.svrmslk.notificationservice.application.usecase.SendEmailNotificationUseCase;
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
public class EmailNotificationRequestedConsumer {

    private static final Logger logger =
            LoggerFactory.getLogger(EmailNotificationRequestedConsumer.class);

    private static final String TENANT_ID_HEADER = "tenantId";
    private static final String CORRELATION_ID_HEADER = "correlationId";
    private static final String DEFAULT_TENANT = "default";

    private final SendEmailNotificationUseCase sendEmailNotificationUseCase;
    private final ObjectMapper objectMapper;

    public EmailNotificationRequestedConsumer(
            SendEmailNotificationUseCase sendEmailNotificationUseCase,
            ObjectMapper objectMapper
    ) {
        this.sendEmailNotificationUseCase = sendEmailNotificationUseCase;
        this.objectMapper = objectMapper;
    }

    /**
     * NOTE:
     * - auth-service publishes to topic "email-notification-requested"
     * - payload is String (JSON)
     */
    @KafkaListener(
            topics = "email-notification-requested",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(
            @Payload String payload,
            @Header(value = TENANT_ID_HEADER, required = false) String tenantIdHeader,
            @Header(value = CORRELATION_ID_HEADER, required = false) String correlationIdHeader,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment
    ) {
        EmailNotificationRequestedEvent event;

        try {
            event = objectMapper.readValue(
                    payload,
                    EmailNotificationRequestedEvent.class
            );
        } catch (Exception e) {
            logger.error(
                    "Failed to deserialize EmailNotificationRequestedEvent. Payload will be ACKed to avoid retry loop.",
                    e
            );
            acknowledgment.acknowledge();
            return;
        }

        String tenantId = extractTenantId(event, tenantIdHeader);
        String correlationId = extractCorrelationId(correlationIdHeader);

        try {
            initializeContext(tenantId, correlationId);

            logger.info(
                    "Received EmailNotificationRequestedEvent from topic={}, partition={}, offset={}, eventId={}",
                    topic, partition, offset, event.getEventId()
            );

            String emailContent = buildEmailContent(event);

            EmailNotificationCommand command = new EmailNotificationCommand(
                    tenantId,
                    event.getEventId(),
                    event.recipientEmail(),
                    event.subject(),
                    emailContent,
                    event.metadata()
            );

            sendEmailNotificationUseCase.execute(command);

            acknowledgment.acknowledge();

            logger.info(
                    "Successfully processed EmailNotificationRequestedEvent, eventId={}",
                    event.getEventId()
            );

        } catch (EmailNotificationFailedException e) {
            logger.error(
                    "Email sending failed for eventId={}. Message NOT acknowledged (retry expected).",
                    event.getEventId(),
                    e
            );

        } catch (Exception e) {
            logger.error(
                    "Unexpected error processing EmailNotificationRequestedEvent, eventId={}. ACKing message.",
                    event.getEventId(),
                    e
            );
            acknowledgment.acknowledge();

        } finally {
            cleanupContext();
        }
    }

    private String buildEmailContent(EmailNotificationRequestedEvent event) {
        StringBuilder content = new StringBuilder();
        content.append("<html><body>");

        if (event.recipientName() != null && !event.recipientName().isBlank()) {
            content.append("<p>Hello ").append(event.recipientName()).append(",</p>");
        }

        if (event.templateVariables() != null && !event.templateVariables().isEmpty()) {
            content.append("<div>");
            event.templateVariables().forEach((key, value) -> {
                content.append("<p><strong>")
                        .append(key)
                        .append(":</strong> ")
                        .append(value)
                        .append("</p>");
            });
            content.append("</div>");
        }

        content.append("</body></html>");
        return content.toString();
    }

    private String extractTenantId(
            EmailNotificationRequestedEvent event,
            String tenantIdHeader
    ) {
        String tenantId =
                (tenantIdHeader != null && !tenantIdHeader.isBlank())
                        ? tenantIdHeader
                        : event.getTenantId();

        if (tenantId == null || tenantId.isBlank()) {
            logger.warn("TenantId missing in event and headers. Falling back to DEFAULT tenant.");
            tenantId = DEFAULT_TENANT;
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
