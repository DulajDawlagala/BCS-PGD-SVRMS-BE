// com/svrmslk/notificationservice/config/ResilienceConfig.java
package com.svrmslk.notificationservice.config;

import com.svrmslk.notificationservice.application.policy.RateLimitPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class ResilienceConfig {

    @Value("${resilience.ratelimit.max-requests-per-window:100}")
    private int maxRequestsPerWindow;

    @Value("${resilience.ratelimit.window-duration-seconds:60}")
    private int windowDurationSeconds;

    @Bean
    public RateLimitPolicy rateLimitPolicy() {
        return new RateLimitPolicy(
                maxRequestsPerWindow,
                Duration.ofSeconds(windowDurationSeconds)
        );
    }
}