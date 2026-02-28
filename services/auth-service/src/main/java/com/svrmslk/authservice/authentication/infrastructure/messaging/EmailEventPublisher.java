package com.svrmslk.authservice.authentication.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svrmslk.authservice.authentication.application.port.out.EmailSenderPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Email sender that publishes events to Kafka for notification-service to consume.
 * Publishes to the correct topic that notification-service is listening to.
 */
@Component
@ConditionalOnProperty(prefix = "events", name = "enabled", havingValue = "true")
public class EmailEventPublisher implements EmailSenderPort {

    private static final Logger logger = LoggerFactory.getLogger(EmailEventPublisher.class);
    private static final String EMAIL_TOPIC = "email-notification-requested";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public EmailEventPublisher(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }



    @Override
    public void sendVerificationEmail(String toEmail, String verificationToken) {
        logger.info("Publishing email verification event for {}", toEmail);

        Map<String, Object> emailEvent = new HashMap<>();
        emailEvent.put("eventId", UUID.randomUUID().toString());
        emailEvent.put("notificationId", "notif-" + System.currentTimeMillis());
        emailEvent.put("tenantId", "default");
        emailEvent.put("recipientEmail", toEmail);
        emailEvent.put("recipientName", "Test User"); // or get actual name
        emailEvent.put("subject", "Email Verification");
        emailEvent.put("templateId", "email-verification");
        emailEvent.put("templateVariables", Map.of(
                "verificationUrl", "http://localhost:3000/verify-email?token=" + verificationToken
        ));
        emailEvent.put("occurredAt", Instant.now().toString());

        publishEvent(emailEvent, toEmail);
    }


    @Override
    public void sendOtpEmail(String toEmail, String otp) {
        logger.info("Publishing OTP email event for {}", toEmail);

        Map<String, Object> emailEvent = new HashMap<>();
        emailEvent.put("eventId", UUID.randomUUID().toString());
        emailEvent.put("eventType", "OTP_EMAIL");
        emailEvent.put("timestamp", Instant.now().toString());
        emailEvent.put("recipientEmail", toEmail);
        emailEvent.put("templateType", "otp-verification");

        Map<String, Object> templateData = new HashMap<>();
        templateData.put("otp", otp);
        emailEvent.put("templateData", templateData);

        publishEvent(emailEvent, toEmail);
    }

    @Override
    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        logger.info("Publishing password reset email event for {}", toEmail);

        Map<String, Object> emailEvent = new HashMap<>();
        emailEvent.put("eventId", UUID.randomUUID().toString());
        emailEvent.put("eventType", "PASSWORD_RESET");
        emailEvent.put("timestamp", Instant.now().toString());
        emailEvent.put("recipientEmail", toEmail);
        emailEvent.put("templateType", "password-reset");

        Map<String, Object> templateData = new HashMap<>();
        templateData.put("resetToken", resetToken);
        templateData.put("resetUrl", "http://localhost:3000/reset-password?token=" + resetToken);
        emailEvent.put("templateData", templateData);

        publishEvent(emailEvent, toEmail);
    }

    private void publishEvent(Map<String, Object> emailEvent, String key) {
        try {
            String eventJson = objectMapper.writeValueAsString(emailEvent);
            kafkaTemplate.send(EMAIL_TOPIC, key, eventJson)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            logger.error("Failed to publish email event to topic {}: {}", EMAIL_TOPIC, ex.getMessage());
                        } else {
                            logger.info("Successfully published email event to topic {}", EMAIL_TOPIC);
                        }
                    });
        } catch (Exception e) {
            logger.error("Failed to serialize email event", e);
            throw new RuntimeException("Failed to publish email event", e);
        }
    }
}