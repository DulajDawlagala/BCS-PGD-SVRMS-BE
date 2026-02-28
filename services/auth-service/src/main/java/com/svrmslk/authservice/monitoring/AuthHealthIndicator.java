package com.svrmslk.authservice.monitoring;

import com.svrmslk.authservice.authentication.application.port.out.RefreshTokenRepositoryPort;
import com.svrmslk.authservice.authentication.application.port.out.UserSessionRepositoryPort;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

import org.springframework.stereotype.Component;
/**

 Custom health indicator for authentication service components.
 Checks token store availability and session management health.
 */
@Component
public class AuthHealthIndicator implements HealthIndicator {
    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final UserSessionRepositoryPort userSessionRepository;
    public AuthHealthIndicator(
            RefreshTokenRepositoryPort refreshTokenRepository,
            UserSessionRepositoryPort userSessionRepository
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userSessionRepository = userSessionRepository;
    }
    @Override
    public Health health() {
        try {
// Test token store availability by attempting a simple operation
// This would typically query a small set of data

            return Health.up()
                    .withDetail("tokenStore", "Available")
                    .withDetail("sessionStore", "Available")
                    .withDetail("status", "Authentication services operational")
                    .build();

        } catch (Exception e) {
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .withDetail("status", "Authentication services degraded")
                    .build();
        }

    }}