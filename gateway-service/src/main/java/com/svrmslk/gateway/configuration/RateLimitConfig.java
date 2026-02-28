package com.svrmslk.gateway.configuration;

import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimitConfig {

    @Bean
    public RedisRateLimiter redisRateLimiter() {
        // 100 requests per 60 seconds
        return new RedisRateLimiter(100, 100, 60);
    }
}