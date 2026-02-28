// com/svrmslk/notificationservice/presentation/NotificationQueryController.java
package com.svrmslk.notificationservice.presentation;

import com.svrmslk.notificationservice.application.port.out.NotificationRepositoryPort;
import com.svrmslk.notificationservice.common.context.ContextHolder;
import com.svrmslk.notificationservice.common.context.TenantContext;
import com.svrmslk.notificationservice.common.security.TenantIsolationViolationException;
import com.svrmslk.notificationservice.common.security.TenantValidator;
import com.svrmslk.notificationservice.domain.model.Notification;
import com.svrmslk.notificationservice.domain.model.NotificationId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationQueryController {
    private static final Logger logger = LoggerFactory.getLogger(NotificationQueryController.class);

    private final NotificationRepositoryPort notificationRepository;

    public NotificationQueryController(NotificationRepositoryPort notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @GetMapping("/{notificationId}")
    public ResponseEntity<NotificationResponse> getNotificationById(
            @PathVariable String notificationId,
            @RequestHeader("X-Tenant-Id") String tenantId
    ) {
        initializeTenantContext(tenantId);

        try {
            TenantValidator.validateTenantAccess(tenantId);

            logger.info("Querying notification by id: {}, tenantId: {}", notificationId, tenantId);

            NotificationId id = NotificationId.of(notificationId);
            Optional<Notification> notification = notificationRepository.findById(id, tenantId);

            if (notification.isEmpty()) {
                logger.info("Notification not found: {}, tenantId: {}", notificationId, tenantId);
                return ResponseEntity.notFound().build();
            }

            NotificationResponse response = toResponse(notification.get());
            return ResponseEntity.ok(response);

        } catch (TenantIsolationViolationException e) {
            logger.error("Tenant isolation violation for notificationId: {}, tenantId: {}", notificationId, tenantId, e);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid notification ID format: {}", notificationId, e);
            return ResponseEntity.badRequest().build();
        } finally {
            ContextHolder.clearAll();
        }
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<NotificationResponse> getNotificationByEventId(
            @PathVariable String eventId,
            @RequestHeader("X-Tenant-Id") String tenantId
    ) {
        initializeTenantContext(tenantId);

        try {
            TenantValidator.validateTenantAccess(tenantId);

            logger.info("Querying notification by eventId: {}, tenantId: {}", eventId, tenantId);

            Optional<Notification> notification = notificationRepository.findByEventIdAndTenantId(eventId, tenantId);

            if (notification.isEmpty()) {
                logger.info("Notification not found for eventId: {}, tenantId: {}", eventId, tenantId);
                return ResponseEntity.notFound().build();
            }

            NotificationResponse response = toResponse(notification.get());
            return ResponseEntity.ok(response);

        } catch (TenantIsolationViolationException e) {
            logger.error("Tenant isolation violation for eventId: {}, tenantId: {}", eventId, tenantId, e);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } finally {
            ContextHolder.clearAll();
        }
    }

    @GetMapping("/event/{eventId}/exists")
    public ResponseEntity<ExistsResponse> checkNotificationExists(
            @PathVariable String eventId,
            @RequestHeader("X-Tenant-Id") String tenantId
    ) {
        initializeTenantContext(tenantId);

        try {
            TenantValidator.validateTenantAccess(tenantId);

            logger.debug("Checking existence for eventId: {}, tenantId: {}", eventId, tenantId);

            boolean exists = notificationRepository.existsByEventIdAndTenantId(eventId, tenantId);
            return ResponseEntity.ok(new ExistsResponse(exists));

        } catch (TenantIsolationViolationException e) {
            logger.error("Tenant isolation violation for eventId: {}, tenantId: {}", eventId, tenantId, e);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } finally {
            ContextHolder.clearAll();
        }
    }

    @GetMapping("/health")
    public ResponseEntity<HealthResponse> health() {
        return ResponseEntity.ok(new HealthResponse("UP"));
    }

    private void initializeTenantContext(String tenantId) {
        ContextHolder.setTenantContext(new TenantContext(tenantId));
    }

    private NotificationResponse toResponse(Notification notification) {
        return new NotificationResponse(
                notification.id().toString(),
                notification.tenantId(),
                notification.eventId(),
                notification.channel().name(),
                notification.recipient(),
                notification.subject(),
                notification.content(),
                notification.metadata(),
                notification.status().name(),
                notification.failureReason(),
                notification.createdAt().toString(),
                notification.updatedAt().toString()
        );
    }

    private record NotificationResponse(
            String id,
            String tenantId,
            String eventId,
            String channel,
            String recipient,
            String subject,
            String content,
            java.util.Map<String, String> metadata,
            String status,
            String failureReason,
            String createdAt,
            String updatedAt
    ) {
    }

    private record ExistsResponse(boolean exists) {
    }

    private record HealthResponse(String status) {
    }
}