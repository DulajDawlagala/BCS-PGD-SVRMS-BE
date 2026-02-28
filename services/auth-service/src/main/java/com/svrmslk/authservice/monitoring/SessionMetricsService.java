package com.svrmslk.authservice.monitoring;

import com.svrmslk.authservice.authentication.application.port.out.UserSessionRepositoryPort;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Service for exposing session-related metrics.
 * Provides gauges for monitoring active session counts.
 */
@Service
public class SessionMetricsService {

    private final DataSource dataSource;

    public SessionMetricsService(DataSource dataSource, MeterRegistry registry) {
        this.dataSource = dataSource;

        // Register gauge for active sessions count
        Gauge.builder("auth.sessions.active", this, SessionMetricsService::getActiveSessionCount)
                .description("Current number of active user sessions")
                .tag("service", "auth")
                .register(registry);

        // Register gauge for total users with active sessions
        Gauge.builder("auth.sessions.active.users", this, SessionMetricsService::getActiveUserCount)
                .description("Current number of users with active sessions")
                .tag("service", "auth")
                .register(registry);
    }

    private long getActiveSessionCount() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM user_sessions WHERE active = true")) {

            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (Exception e) {
            // Log error but don't fail metrics collection
            return -1;
        }
        return 0;
    }

    private long getActiveUserCount() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT COUNT(DISTINCT user_id) FROM user_sessions WHERE active = true")) {

            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (Exception e) {
            return -1;
        }
        return 0;
    }
}