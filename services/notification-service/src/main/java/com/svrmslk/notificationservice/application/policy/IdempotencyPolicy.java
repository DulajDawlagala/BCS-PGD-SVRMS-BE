// com/svrmslk/notificationservice/application/policy/IdempotencyPolicy.java
package com.svrmslk.notificationservice.application.policy;
import org.springframework.stereotype.Component;

import com.svrmslk.notificationservice.application.port.out.NotificationRepositoryPort;
import com.svrmslk.notificationservice.domain.model.Notification;

import java.util.Optional;
@Component
public final class IdempotencyPolicy {
    private final NotificationRepositoryPort notificationRepository;

    public IdempotencyPolicy(NotificationRepositoryPort notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public IdempotencyCheckResult checkIdempotency(String eventId, String tenantId) {
        if (eventId == null || eventId.isBlank()) {
            throw new IllegalArgumentException("EventId cannot be null or blank");
        }
        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalArgumentException("TenantId cannot be null or blank");
        }

        Optional<Notification> existing = notificationRepository.findByEventIdAndTenantId(eventId, tenantId);

        if (existing.isPresent()) {
            return IdempotencyCheckResult.duplicate(existing.get());
        }

        return IdempotencyCheckResult.unique();
    }

    public static final class IdempotencyCheckResult {
        private final boolean isDuplicate;
        private final Notification existingNotification;

        private IdempotencyCheckResult(boolean isDuplicate, Notification existingNotification) {
            this.isDuplicate = isDuplicate;
            this.existingNotification = existingNotification;
        }

        public static IdempotencyCheckResult unique() {
            return new IdempotencyCheckResult(false, null);
        }

        public static IdempotencyCheckResult duplicate(Notification existingNotification) {
            return new IdempotencyCheckResult(true, existingNotification);
        }

        public boolean isDuplicate() {
            return isDuplicate;
        }

        public boolean isUnique() {
            return !isDuplicate;
        }

        public Optional<Notification> getExistingNotification() {
            return Optional.ofNullable(existingNotification);
        }
    }
}