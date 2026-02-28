// com/svrmslk/notificationservice/infrastructure/external/email/SendGridEmailAdapter.java
package com.svrmslk.notificationservice.infrastructure.external.email;

import com.svrmslk.notificationservice.application.port.out.EmailSendException;
import com.svrmslk.notificationservice.application.port.out.EmailSenderPort;
import com.svrmslk.notificationservice.common.exception.ExternalProviderException;
import com.svrmslk.notificationservice.domain.model.Notification;
import com.svrmslk.notificationservice.domain.model.NotificationChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SendGridEmailAdapter implements EmailSenderPort {
    private static final Logger logger = LoggerFactory.getLogger(SendGridEmailAdapter.class);

    private final SendGridClient sendGridClient;

    public SendGridEmailAdapter(SendGridClient sendGridClient) {
        this.sendGridClient = sendGridClient;
    }

    @Override
    public void sendEmail(Notification notification) throws EmailSendException {
        validateNotification(notification);

        try {
            logger.debug(
                    "Attempting to send email via SendGrid for notificationId: {}, tenantId: {}",
                    notification.id(), notification.tenantId()
            );

            sendGridClient.sendEmail(
                    notification.recipient(),
                    notification.subject(),
                    notification.content()
            );

            logger.info(
                    "Email sent successfully for notificationId: {}, eventId: {}",
                    notification.id(), notification.eventId()
            );

        } catch (ExternalProviderException e) {
            logger.error(
                    "Failed to send email for notificationId: {}, eventId: {}, provider: {}",
                    notification.id(), notification.eventId(), e.getProvider(), e
            );
            throw new EmailSendException(
                    "SendGrid failed to send email: " + e.getMessage(),
                    e
            );
        } catch (Exception e) {
            logger.error(
                    "Unexpected error sending email for notificationId: {}, eventId: {}",
                    notification.id(), notification.eventId(), e
            );
            throw new EmailSendException(
                    "Unexpected error sending email: " + e.getMessage(),
                    e
            );
        }
    }

    private void validateNotification(Notification notification) throws EmailSendException {
        if (notification == null) {
            throw new EmailSendException("Notification cannot be null");
        }

        if (notification.channel() != NotificationChannel.EMAIL) {
            throw new EmailSendException(
                    "Invalid channel for email adapter: " + notification.channel()
            );
        }

        if (notification.recipient() == null || notification.recipient().isBlank()) {
            throw new EmailSendException("Recipient email cannot be null or blank");
        }

        if (notification.subject() == null || notification.subject().isBlank()) {
            throw new EmailSendException("Email subject cannot be null or blank");
        }

        if (notification.content() == null || notification.content().isBlank()) {
            throw new EmailSendException("Email content cannot be null or blank");
        }
    }
}