

package com.svrmslk.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Component
public class UserContextFilter implements GlobalFilter, Ordered {

    private final WebClient webClient;

    public UserContextFilter(
            WebClient.Builder webClientBuilder,
            @Value("${services.company-service.url}") String companyServiceUrl
    ) {
        this.webClient = webClientBuilder.baseUrl(companyServiceUrl).build();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String userId = exchange.getRequest().getHeaders().getFirst("X-USER-ID");
        String path = exchange.getRequest().getPath().value();

        if (userId == null || userId.isBlank()) {
            return chain.filter(exchange);
        }

        log.debug("Resolving tenant context for user {} -> {}", userId, path);

        return webClient.get()
                .uri("/internal/tenant-context")
                .header("X-USER-ID", userId)
                .retrieve()
                .bodyToMono(Map.class)
                .onErrorResume(ex -> {
                    log.warn("Tenant resolution failed for user {}: {}", userId, ex.getMessage());
                    return Mono.empty();
                })
                .flatMap(tenantCtx -> {
                    if (tenantCtx == null) {
                        return chain.filter(exchange);
                    }

                    ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                            .header("X-TENANT-ID", String.valueOf(tenantCtx.getOrDefault("tenantId", "")))
                            .header("X-TENANT-TYPE", String.valueOf(tenantCtx.getOrDefault("tenantType", "")))
                            .header("X-EFFECTIVE-ROLE", String.valueOf(tenantCtx.getOrDefault("effectiveRole", "")))
                            .build();

                    return chain.filter(exchange.mutate().request(mutatedRequest).build());
                })
                .switchIfEmpty(chain.filter(exchange));
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
