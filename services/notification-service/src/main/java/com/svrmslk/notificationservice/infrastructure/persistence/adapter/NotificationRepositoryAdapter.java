// com/svrmslk/notificationservice/infrastructure/persistence/adapter/NotificationRepositoryAdapter.java
package com.svrmslk.notificationservice.infrastructure.persistence.adapter;

import com.svrmslk.notificationservice.application.port.out.NotificationRepositoryPort;
import com.svrmslk.notificationservice.common.security.TenantValidator;
import com.svrmslk.notificationservice.domain.model.Notification;
import com.svrmslk.notificationservice.domain.model.NotificationChannel;
import com.svrmslk.notificationservice.domain.model.NotificationId;
import com.svrmslk.notificationservice.domain.model.NotificationStatus;
import com.svrmslk.notificationservice.infrastructure.persistence.entity.NotificationEntity;
import com.svrmslk.notificationservice.infrastructure.persistence.repository.JpaNotificationRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class NotificationRepositoryAdapter implements NotificationRepositoryPort {

    private final JpaNotificationRepository jpaRepository;

    public NotificationRepositoryAdapter(JpaNotificationRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Notification save(Notification notification) {
        TenantValidator.validateTenantAccess(notification.tenantId());

        NotificationEntity entity = toEntity(notification);
        NotificationEntity savedEntity = jpaRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Notification> findById(NotificationId id, String tenantId) {
        TenantValidator.validateTenantAccess(tenantId);

        return jpaRepository.findByIdAndTenantId(id.value(), tenantId)
                .map(this::toDomain);
    }

    @Override
    public boolean existsByEventIdAndTenantId(String eventId, String tenantId) {
        TenantValidator.validateTenantAccess(tenantId);

        return jpaRepository.existsByEventIdAndTenantId(eventId, tenantId);
    }

    @Override
    public Optional<Notification> findByEventIdAndTenantId(String eventId, String tenantId) {
        TenantValidator.validateTenantAccess(tenantId);

        return jpaRepository.findByEventIdAndTenantId(eventId, tenantId)
                .map(this::toDomain);
    }

    private NotificationEntity toEntity(Notification notification) {
        NotificationEntity entity = new NotificationEntity();
        entity.setId(notification.id().value());
        entity.setTenantId(notification.tenantId());
        entity.setEventId(notification.eventId());
        entity.setChannel(toEntityChannel(notification.channel()));
        entity.setRecipient(notification.recipient());
        entity.setSubject(notification.subject());
        entity.setContent(notification.content());
        entity.setMetadata(notification.metadata());
        entity.setStatus(toEntityStatus(notification.status()));
        entity.setFailureReason(notification.failureReason());
        entity.setCreatedAt(notification.createdAt());
        entity.setUpdatedAt(notification.updatedAt());
        return entity;
    }

    private Notification toDomain(NotificationEntity entity) {
        return Notification.create(
                entity.getTenantId(),
                entity.getEventId(),
                toDomainChannel(entity.getChannel()),
                entity.getRecipient(),
                entity.getSubject(),
                entity.getContent(),
                entity.getMetadata()
        );
    }

    private NotificationEntity.ChannelType toEntityChannel(NotificationChannel channel) {
        return switch (channel) {
            case EMAIL -> NotificationEntity.ChannelType.EMAIL;
            case IN_APP -> NotificationEntity.ChannelType.IN_APP;
        };
    }

    private NotificationChannel toDomainChannel(NotificationEntity.ChannelType channelType) {
        return switch (channelType) {
            case EMAIL -> NotificationChannel.EMAIL;
            case IN_APP -> NotificationChannel.IN_APP;
        };
    }

    private NotificationEntity.StatusType toEntityStatus(NotificationStatus status) {
        return switch (status) {
            case PENDING -> NotificationEntity.StatusType.PENDING;
            case SENT -> NotificationEntity.StatusType.SENT;
            case FAILED -> NotificationEntity.StatusType.FAILED;
        };
    }

    private NotificationStatus toDomainStatus(NotificationEntity.StatusType statusType) {
        return switch (statusType) {
            case PENDING -> NotificationStatus.PENDING;
            case SENT -> NotificationStatus.SENT;
            case FAILED -> NotificationStatus.FAILED;
        };
    }
}