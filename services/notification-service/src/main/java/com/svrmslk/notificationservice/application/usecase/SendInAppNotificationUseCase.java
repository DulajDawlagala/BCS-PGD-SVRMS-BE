package com.svrmslk.notificationservice.application.usecase;

import com.svrmslk.notificationservice.application.policy.IdempotencyPolicy;
import com.svrmslk.notificationservice.application.policy.IdempotencyPolicy.IdempotencyCheckResult;
import com.svrmslk.notificationservice.application.policy.RateLimitExceededException;
import com.svrmslk.notificationservice.application.policy.RateLimitPolicy;
import com.svrmslk.notificationservice.application.port.in.InAppNotificationCommand;
import com.svrmslk.notificationservice.application.port.out.InAppNotificationException;
import com.svrmslk.notificationservice.application.port.out.InAppNotifierPort;
import com.svrmslk.notificationservice.application.port.out.NotificationRepositoryPort;
import com.svrmslk.notificationservice.domain.model.Notification;
import com.svrmslk.notificationservice.domain.service.NotificationComposer;
import org.springframework.stereotype.Service;

@Service
public final class SendInAppNotificationUseCase {

    private final NotificationComposer notificationComposer;
    private final InAppNotifierPort inAppNotifier;
    private final NotificationRepositoryPort notificationRepository;
    private final IdempotencyPolicy idempotencyPolicy;
    private final RateLimitPolicy rateLimitPolicy;

    public SendInAppNotificationUseCase(
            NotificationComposer notificationComposer,
            InAppNotifierPort inAppNotifier,
            NotificationRepositoryPort notificationRepository,
            IdempotencyPolicy idempotencyPolicy,
            RateLimitPolicy rateLimitPolicy
    ) {
        this.notificationComposer = notificationComposer;
        this.inAppNotifier = inAppNotifier;
        this.notificationRepository = notificationRepository;
        this.idempotencyPolicy = idempotencyPolicy;
        this.rateLimitPolicy = rateLimitPolicy;
    }

    public void execute(InAppNotificationCommand command)
            throws RateLimitExceededException {

        // 1️⃣ Idempotency
        IdempotencyCheckResult idempotencyResult =
                idempotencyPolicy.checkIdempotency(
                        command.eventId(),
                        command.tenantId()
                );

        if (idempotencyResult.isDuplicate()) {
            return;
        }

        // 2️⃣ Rate limiting
        if (!rateLimitPolicy.allowRequest(
                command.tenantId(),
                command.recipient()
        )) {
            throw new RateLimitExceededException(
                    command.tenantId(),
                    command.recipient()
            );
        }

        // 3️⃣ Compose domain object
        Notification notification = notificationComposer.composeInApp(
                command.tenantId(),
                command.eventId(),
                command.recipient(),
                command.content(),
                command.metadata()
        );

        // 4️⃣ Persist before sending
        Notification savedNotification =
                notificationRepository.save(notification);

        // 5️⃣ Send + update state
        try {
            inAppNotifier.sendInAppNotification(savedNotification);

            savedNotification.markAsSent();
            notificationRepository.save(savedNotification);

        } catch (InAppNotificationException e) {

            savedNotification.markAsFailed(e.getMessage());
            notificationRepository.save(savedNotification);

            throw new InAppNotificationFailedException(
                    "Failed to send in-app notification for eventId: "
                            + command.eventId(),
                    e
            );
        }
    }
}
