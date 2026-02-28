package com.svrmslk.gateway.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.WebFilter;

@Configuration
public class SecurityHeadersConfig {

    @Bean
    public WebFilter securityHeadersFilter() {
        return (exchange, chain) -> {
            exchange.getResponse().getHeaders().add("X-Frame-Options", "DENY");
            exchange.getResponse().getHeaders().add("X-Content-Type-Options", "nosniff");
            exchange.getResponse().getHeaders().add("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
            exchange.getResponse().getHeaders().add("X-XSS-Protection", "1; mode=block");
            return chain.filter(exchange);
        };
    }
}