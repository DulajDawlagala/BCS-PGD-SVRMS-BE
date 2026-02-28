package com.svrmslk.authservice.authentication.infrastructure.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Filter for rate limiting authentication endpoints.
 * Implements sliding window rate limiting per IP address.
 */
@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitingFilter.class);

    private static final int LOGIN_LIMIT = 15;
    private static final int OTP_LIMIT = 3;
    private static final int VERIFICATION_LIMIT = 3;
    private static final long WINDOW_SECONDS = 300; // 5 minutes

    private final Map<String, RateLimitBucket> rateLimits = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();
        String ipAddress = getClientIpAddress(request);

        int limit = determineLimit(path);
        if (limit > 0) {
            String key = ipAddress + ":" + path;

            if (isRateLimitExceeded(key, limit)) {
                logger.warn("Rate limit exceeded for IP: {} on path: {}", ipAddress, path);
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.getWriter().write("{\"error\":\"Rate limit exceeded. Please try again later.\"}");
                response.setContentType("application/json");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private int determineLimit(String path) {
        if (path.endsWith("/login")) {
            return LOGIN_LIMIT;
        } else if (path.contains("/otp")) {
            return OTP_LIMIT;
        } else if (path.contains("/verify")) {
            return VERIFICATION_LIMIT;
        }
        return 0;
    }

    private boolean isRateLimitExceeded(String key, int limit) {
        RateLimitBucket bucket = rateLimits.computeIfAbsent(key, k -> new RateLimitBucket());
        return !bucket.allowRequest(limit);
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private static class RateLimitBucket {
        private final AtomicInteger count = new AtomicInteger(0);
        private volatile long windowStart = Instant.now().getEpochSecond();

        synchronized boolean allowRequest(int limit) {
            long now = Instant.now().getEpochSecond();

            if (now - windowStart >= WINDOW_SECONDS) {
                windowStart = now;
                count.set(0);
            }

            int current = count.incrementAndGet();
            return current <= limit;
        }
    }
}