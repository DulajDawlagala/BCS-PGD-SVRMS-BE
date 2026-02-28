// com/svrmslk/notificationservice/presentation/NotificationWebSocketController.java
package com.svrmslk.notificationservice.presentation;

import com.svrmslk.notificationservice.infrastructure.websocket.InAppWebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/websocket")
public class NotificationWebSocketController {
    private static final Logger logger = LoggerFactory.getLogger(NotificationWebSocketController.class);

    private final InAppWebSocketHandler webSocketHandler;

    public NotificationWebSocketController(InAppWebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }

    @GetMapping("/status/{userId}")
    public ResponseEntity<WebSocketStatusResponse> getUserWebSocketStatus(
            @PathVariable String userId,
            @RequestHeader("X-Tenant-Id") String tenantId
    ) {
        if (tenantId == null || tenantId.isBlank()) {
            logger.warn("Missing X-Tenant-Id header");
            return ResponseEntity.badRequest().build();
        }

        if (userId == null || userId.isBlank()) {
            logger.warn("Invalid userId provided");
            return ResponseEntity.badRequest().build();
        }

        try {
            boolean isConnected = webSocketHandler.hasActiveSession(tenantId, userId);

            logger.debug(
                    "WebSocket status check - tenantId: {}, userId: {}, connected: {}",
                    tenantId, userId, isConnected
            );

            return ResponseEntity.ok(new WebSocketStatusResponse(
                    userId,
                    tenantId,
                    isConnected
            ));

        } catch (Exception e) {
            logger.error("Error checking WebSocket status for userId: {}, tenantId: {}", userId, tenantId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/health")
    public ResponseEntity<HealthResponse> health() {
        return ResponseEntity.ok(new HealthResponse("UP"));
    }

    private record WebSocketStatusResponse(
            String userId,
            String tenantId,
            boolean connected
    ) {
    }

    private record HealthResponse(String status) {
    }
}