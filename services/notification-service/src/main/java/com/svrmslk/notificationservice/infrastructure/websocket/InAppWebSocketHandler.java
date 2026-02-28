// com/svrmslk/notificationservice/infrastructure/websocket/InAppWebSocketHandler.java
package com.svrmslk.notificationservice.infrastructure.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
public class InAppWebSocketHandler extends TextWebSocketHandler {
    private static final Logger logger = LoggerFactory.getLogger(InAppWebSocketHandler.class);
    private static final String TENANT_ID_ATTR = "tenantId";
    private static final String USER_ID_ATTR = "userId";

    private final Map<String, CopyOnWriteArraySet<WebSocketSession>> tenantUserSessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    public InAppWebSocketHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String tenantId = extractAttribute(session, TENANT_ID_ATTR);
        String userId = extractAttribute(session, USER_ID_ATTR);

        if (tenantId == null || userId == null) {
            logger.warn("Connection rejected - missing tenantId or userId in session attributes");
            session.close(CloseStatus.POLICY_VIOLATION);
            return;
        }

        String key = buildKey(tenantId, userId);
        tenantUserSessions.computeIfAbsent(key, k -> new CopyOnWriteArraySet<>()).add(session);

        logger.info(
                "WebSocket connection established for tenantId: {}, userId: {}, sessionId: {}",
                maskId(tenantId), maskId(userId), session.getId()
        );
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String tenantId = extractAttribute(session, TENANT_ID_ATTR);
        String userId = extractAttribute(session, USER_ID_ATTR);

        if (tenantId != null && userId != null) {
            String key = buildKey(tenantId, userId);
            CopyOnWriteArraySet<WebSocketSession> sessions = tenantUserSessions.get(key);

            if (sessions != null) {
                sessions.remove(session);
                if (sessions.isEmpty()) {
                    tenantUserSessions.remove(key);
                }
            }

            logger.info(
                    "WebSocket connection closed for tenantId: {}, userId: {}, sessionId: {}, status: {}",
                    maskId(tenantId), maskId(userId), session.getId(), status
            );
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        String tenantId = extractAttribute(session, TENANT_ID_ATTR);
        String userId = extractAttribute(session, USER_ID_ATTR);

        logger.error(
                "WebSocket transport error for tenantId: {}, userId: {}, sessionId: {}",
                maskId(tenantId), maskId(userId), session.getId(), exception
        );

        if (session.isOpen()) {
            session.close(CloseStatus.SERVER_ERROR);
        }
    }

    public void sendMessageToUser(String tenantId, String userId, Object message) throws InAppDeliveryException {
        if (tenantId == null || tenantId.isBlank()) {
            throw new InAppDeliveryException("TenantId cannot be null or blank");
        }
        if (userId == null || userId.isBlank()) {
            throw new InAppDeliveryException("UserId cannot be null or blank");
        }
        if (message == null) {
            throw new InAppDeliveryException("Message cannot be null");
        }

        String key = buildKey(tenantId, userId);
        CopyOnWriteArraySet<WebSocketSession> sessions = tenantUserSessions.get(key);

        if (sessions == null || sessions.isEmpty()) {
            throw new InAppDeliveryException(
                    "No active WebSocket sessions for tenantId: " + tenantId + ", userId: " + userId
            );
        }

        String messageJson;
        try {
            messageJson = objectMapper.writeValueAsString(message);
        } catch (Exception e) {
            throw new InAppDeliveryException("Failed to serialize message to JSON", e);
        }

        TextMessage textMessage = new TextMessage(messageJson);
        int successCount = 0;
        int failureCount = 0;

        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(textMessage);
                    successCount++;
                } catch (IOException e) {
                    failureCount++;
                    logger.error(
                            "Failed to send message to session: {}, tenantId: {}, userId: {}",
                            session.getId(), maskId(tenantId), maskId(userId), e
                    );
                }
            } else {
                sessions.remove(session);
            }
        }

        if (successCount == 0) {
            throw new InAppDeliveryException(
                    "Failed to deliver message to any active sessions for tenantId: " + tenantId + ", userId: " + userId
            );
        }

        logger.info(
                "Message delivered to user - tenantId: {}, userId: {}, successCount: {}, failureCount: {}",
                maskId(tenantId), maskId(userId), successCount, failureCount
        );
    }

    public boolean hasActiveSession(String tenantId, String userId) {
        String key = buildKey(tenantId, userId);
        CopyOnWriteArraySet<WebSocketSession> sessions = tenantUserSessions.get(key);
        return sessions != null && !sessions.isEmpty() && sessions.stream().anyMatch(WebSocketSession::isOpen);
    }

    private String buildKey(String tenantId, String userId) {
        return tenantId + ":" + userId;
    }

    private String extractAttribute(WebSocketSession session, String attributeName) {
        Object value = session.getAttributes().get(attributeName);
        return value != null ? value.toString() : null;
    }

    private String maskId(String id) {
        if (id == null || id.length() <= 4) {
            return "***";
        }
        return id.substring(0, 2) + "***" + id.substring(id.length() - 2);
    }

    public static class InAppDeliveryException extends Exception {
        public InAppDeliveryException(String message) {
            super(message);
        }

        public InAppDeliveryException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}