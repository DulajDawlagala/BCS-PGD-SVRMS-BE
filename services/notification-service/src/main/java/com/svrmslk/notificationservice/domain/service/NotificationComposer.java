// com/svrmslk/notificationservice/domain/service/NotificationComposer.java
package com.svrmslk.notificationservice.domain.service;

import com.svrmslk.notificationservice.domain.model.Notification;
import com.svrmslk.notificationservice.domain.model.NotificationChannel;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public final class NotificationComposer {

    public Notification composeEmail(
            String tenantId,
            String eventId,
            String recipient,
            String subject,
            String content,
            Map<String, String> metadata
    ) {
        return Notification.create(
                tenantId,
                eventId,
                NotificationChannel.EMAIL,
                recipient,
                subject,
                content,
                metadata
        );
    }

    public Notification composeInApp(
            String tenantId,
            String eventId,
            String recipient,
            String content,
            Map<String, String> metadata
    ) {
        return Notification.create(
                tenantId,
                eventId,
                NotificationChannel.IN_APP,
                recipient,
                null,
                content,
                metadata
        );
    }
}