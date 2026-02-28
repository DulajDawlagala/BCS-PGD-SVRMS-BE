// com/svrmslk/notificationservice/application/usecase/SendEmailNotificationUseCase.java
package com.svrmslk.notificationservice.application.usecase;

import com.svrmslk.notificationservice.application.policy.IdempotencyPolicy;
import com.svrmslk.notificationservice.application.policy.IdempotencyPolicy.IdempotencyCheckResult;
import com.svrmslk.notificationservice.application.policy.RateLimitExceededException;
import com.svrmslk.notificationservice.application.policy.RateLimitPolicy;
import com.svrmslk.notificationservice.application.port.in.EmailNotificationCommand;
import com.svrmslk.notificationservice.application.port.out.EmailSendException;
import com.svrmslk.notificationservice.application.port.out.EmailSenderPort;
import com.svrmslk.notificationservice.application.port.out.NotificationRepositoryPort;
import com.svrmslk.notificationservice.domain.model.Notification;
import com.svrmslk.notificationservice.domain.service.NotificationComposer;
import org.springframework.stereotype.Service;

@Service
public final class SendEmailNotificationUseCase {
    private final NotificationComposer notificationComposer;
    private final EmailSenderPort emailSender;
    private final NotificationRepositoryPort notificationRepository;
    private final IdempotencyPolicy idempotencyPolicy;
    private final RateLimitPolicy rateLimitPolicy;

    public SendEmailNotificationUseCase(
            NotificationComposer notificationComposer,
            EmailSenderPort emailSender,
            NotificationRepositoryPort notificationRepository,
            IdempotencyPolicy idempotencyPolicy,
            RateLimitPolicy rateLimitPolicy
    ) {
        this.notificationComposer = notificationComposer;
        this.emailSender = emailSender;
        this.notificationRepository = notificationRepository;
        this.idempotencyPolicy = idempotencyPolicy;
        this.rateLimitPolicy = rateLimitPolicy;
    }

    public void execute(EmailNotificationCommand command) throws RateLimitExceededException {
        IdempotencyCheckResult idempotencyResult = idempotencyPolicy.checkIdempotency(
                command.eventId(),
                command.tenantId()
        );

        if (idempotencyResult.isDuplicate()) {
            return;
        }

        if (!rateLimitPolicy.allowRequest(command.tenantId(), command.recipient())) {
            throw new RateLimitExceededException(command.tenantId(), command.recipient());
        }

        Notification notification = notificationComposer.composeEmail(
                command.tenantId(),
                command.eventId(),
                command.recipient(),
                command.subject(),
                command.content(),
                command.metadata()
        );

        Notification savedNotification = notificationRepository.save(notification);

        try {
            emailSender.sendEmail(savedNotification);
            savedNotification.markAsSent();
            notificationRepository.save(savedNotification);
        } catch (EmailSendException e) {
            savedNotification.markAsFailed(e.getMessage());
            notificationRepository.save(savedNotification);
            throw new EmailNotificationFailedException(
                    "Failed to send email notification for eventId: " + command.eventId(),
                    e
            );
        }
    }
}