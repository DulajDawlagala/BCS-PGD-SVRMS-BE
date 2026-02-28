// com/svrmslk/notificationservice/infrastructure/websocket/InAppWebSocketAdapter.java
package com.svrmslk.notificationservice.infrastructure.websocket;

import com.svrmslk.notificationservice.application.port.out.InAppNotificationException;
import com.svrmslk.notificationservice.application.port.out.InAppNotifierPort;
import com.svrmslk.notificationservice.domain.model.Notification;
import com.svrmslk.notificationservice.domain.model.NotificationChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
public class InAppWebSocketAdapter implements InAppNotifierPort {
    private static final Logger logger = LoggerFactory.getLogger(InAppWebSocketAdapter.class);

    private final InAppWebSocketHandler webSocketHandler;

    public InAppWebSocketAdapter(InAppWebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }

    @Override
    public void sendInAppNotification(Notification notification) throws InAppNotificationException {
        validateNotification(notification);

        String userId = notification.recipient();
        String tenantId = notification.tenantId();

        if (!webSocketHandler.hasActiveSession(tenantId, userId)) {
            logger.warn(
                    "No active WebSocket session for notificationId: {}, tenantId: {}, userId: {}. " +
                            "User is offline - notification will not be delivered.",
                    notification.id(), tenantId, userId
            );
            throw new InAppNotificationException(
                    "User is not connected - cannot deliver in-app notification"
            );
        }

        InAppMessage message = buildMessage(notification);

        try {
            logger.debug(
                    "Attempting to send in-app notification via WebSocket for notificationId: {}, tenantId: {}, userId: {}",
                    notification.id(), tenantId, userId
            );

            webSocketHandler.sendMessageToUser(tenantId, userId, message);

            logger.info(
                    "In-app notification sent successfully for notificationId: {}, eventId: {}",
                    notification.id(), notification.eventId()
            );

        } catch (InAppWebSocketHandler.InAppDeliveryException e) {
            logger.error(
                    "Failed to deliver in-app notification for notificationId: {}, eventId: {}",
                    notification.id(), notification.eventId(), e
            );
            throw new InAppNotificationException(
                    "Failed to deliver in-app notification: " + e.getMessage(),
                    e
            );
        } catch (Exception e) {
            logger.error(
                    "Unexpected error sending in-app notification for notificationId: {}, eventId: {}",
                    notification.id(), notification.eventId(), e
            );
            throw new InAppNotificationException(
                    "Unexpected error sending in-app notification: " + e.getMessage(),
                    e
            );
        }
    }

    private void validateNotification(Notification notification) throws InAppNotificationException {
        if (notification == null) {
            throw new InAppNotificationException("Notification cannot be null");
        }

        if (notification.channel() != NotificationChannel.IN_APP) {
            throw new InAppNotificationException(
                    "Invalid channel for in-app adapter: " + notification.channel()
            );
        }

        if (notification.recipient() == null || notification.recipient().isBlank()) {
            throw new InAppNotificationException("Recipient userId cannot be null or blank");
        }

        if (notification.content() == null || notification.content().isBlank()) {
            throw new InAppNotificationException("Notification content cannot be null or blank");
        }
    }

    private InAppMessage buildMessage(Notification notification) {
        return new InAppMessage(
                notification.id().toString(),
                notification.eventId(),
                notification.content(),
                notification.metadata(),
                Instant.now().toString()
        );
    }

    private record InAppMessage(
            String notificationId,
            String eventId,
            String content,
            Map<String, String> metadata,
            String timestamp
    ) {
    }
}