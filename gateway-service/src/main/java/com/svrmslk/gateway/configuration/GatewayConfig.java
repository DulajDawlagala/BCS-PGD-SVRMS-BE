package com.svrmslk.gateway.configuration;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Configuration
public class GatewayConfig {

    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange -> {
            String clientIp = Objects.requireNonNull(
                    exchange.getRequest().getRemoteAddress()
            ).getAddress().getHostAddress();
            return Mono.just(clientIp);
        };
    }
}