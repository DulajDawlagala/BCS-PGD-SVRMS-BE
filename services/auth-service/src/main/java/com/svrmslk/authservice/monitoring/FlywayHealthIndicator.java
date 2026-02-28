package com.svrmslk.authservice.monitoring;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.MigrationInfoService;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Custom health indicator for Flyway database migrations.
 * Checks migration status and reports pending or failed migrations.
 */
@Component
public class FlywayHealthIndicator implements HealthIndicator {

    private final Flyway flyway;

    public FlywayHealthIndicator(Flyway flyway) {
        this.flyway = flyway;
    }

    @Override
    public Health health() {
        try {
            MigrationInfoService migrationInfoService = flyway.info();
            MigrationInfo[] migrations = migrationInfoService.all();
            MigrationInfo current = migrationInfoService.current();
            MigrationInfo[] pending = migrationInfoService.pending();

            if (pending.length > 0) {
                return Health.down()
                        .withDetail("status", "Pending migrations detected")
                        .withDetail("pendingCount", pending.length)
                        .withDetail("currentVersion", current != null ? current.getVersion().getVersion() : "none")
                        .build();
            }

            if (current == null) {
                return Health.down()
                        .withDetail("status", "No migrations applied")
                        .withDetail("totalMigrations", migrations.length)
                        .build();
            }

            return Health.up()
                    .withDetail("status", "All migrations applied")
                    .withDetail("currentVersion", current.getVersion().getVersion())
                    .withDetail("appliedMigrations", migrations.length)
                    .withDetail("pendingMigrations", pending.length)
                    .build();

        } catch (Exception e) {
            return Health.down()
                    .withDetail("status", "Flyway check failed")
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}