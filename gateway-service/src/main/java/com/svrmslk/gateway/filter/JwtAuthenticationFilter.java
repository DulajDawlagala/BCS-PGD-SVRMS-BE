

package com.svrmslk.gateway.filter;

import com.svrmslk.gateway.configuration.JwtConfig;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    private final JwtConfig jwtConfig;

    public JwtAuthenticationFilter(JwtConfig jwtConfig) {
        super(Config.class);
        this.jwtConfig = jwtConfig;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // Skip JWT validation for auth endpoints
            if (request.getPath().value().startsWith("/api/v1/auth/")) {
                return chain.filter(exchange);
            }

            String token = null;

            // 1️⃣ Try Authorization header first
            String authHeader = request.getHeaders().getFirst("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            }

            // 2️⃣ If not found, try HttpOnly cookie
            if (token == null) {
                var cookies = request.getCookies().getFirst("access_token");
                if (cookies != null) {
                    token = cookies.getValue();
                }
            }

            // 3️⃣ If still null → unauthorized
            if (token == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing token");
            }

            try {
                Claims claims = jwtConfig.validateAndParseClaims(token);

                if (jwtConfig.isTokenExpired(claims)) {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token expired");
                }

                // Extract identity information only
                String userId = jwtConfig.getUserId(claims);
                String email = jwtConfig.getEmail(claims);
                String sessionId = jwtConfig.getSessionId(claims);
                List<String> globalRoles = jwtConfig.getGlobalRoles(claims);

                // Build modified request with identity headers only
                ServerHttpRequest modifiedRequest = request.mutate()
                        .header("X-USER-ID", userId != null ? userId : "")
                        .header("X-EMAIL", email != null ? email : "")
                        .header("X-SESSION-ID", sessionId != null ? sessionId : "")
                        .header("X-GLOBAL-ROLES", globalRoles != null ? String.join(",", globalRoles) : "")
                        .build();

                log.debug("JWT validated for user: {}", userId);

                return chain.filter(exchange.mutate().request(modifiedRequest).build());

            } catch (ResponseStatusException e) {
                throw e;
            } catch (Exception e) {
                log.error("JWT validation error: {}", e.getMessage());
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
            }
        };
    }

    public static class Config {
        // Configuration properties if needed
    }
}

