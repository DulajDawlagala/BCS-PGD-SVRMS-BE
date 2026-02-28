// com/svrmslk/notificationservice/config/WebSocketConfig.java
package com.svrmslk.notificationservice.config;

import com.svrmslk.notificationservice.infrastructure.websocket.InAppWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final InAppWebSocketHandler inAppWebSocketHandler;

    public WebSocketConfig(InAppWebSocketHandler inAppWebSocketHandler) {
        this.inAppWebSocketHandler = inAppWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(inAppWebSocketHandler, "/ws/notifications")
                .setAllowedOrigins("*");
    }
}