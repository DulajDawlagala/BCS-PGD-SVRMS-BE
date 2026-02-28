package com.svrmslk.authservice.monitoring;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Custom health indicator for database connectivity.
 * Verifies database connection and basic query execution.
 */
@Component
public class DatabaseHealthIndicator implements HealthIndicator {

    private final DataSource dataSource;

    public DatabaseHealthIndicator(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Health health() {
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(1)) {
                // Execute a simple query to verify database responsiveness
                try (Statement statement = connection.createStatement();
                     ResultSet resultSet = statement.executeQuery("SELECT 1")) {

                    if (resultSet.next()) {
                        return Health.up()
                                .withDetail("database", "PostgreSQL")
                                .withDetail("status", "Connection successful")
                                .withDetail("validationQuery", "SELECT 1")
                                .build();
                    }
                }
            }

            return Health.down()
                    .withDetail("database", "PostgreSQL")
                    .withDetail("status", "Connection failed validation")
                    .build();

        } catch (Exception e) {
            return Health.down()
                    .withDetail("database", "PostgreSQL")
                    .withDetail("error", e.getMessage())
                    .withDetail("status", "Unable to connect")
                    .build();
        }
    }
}
