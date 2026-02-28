# Use current directory as base path
$basePath = Get-Location

# Helper: create directory if not exists
function New-Dir($path) {
    if (-not (Test-Path $path)) {
        New-Item -ItemType Directory -Path $path -Force | Out-Null
    }
}

# Helper: create file if not exists
function New-File($path) {
    if (-not (Test-Path $path)) {
        New-Item -ItemType File -Path $path -Force | Out-Null
    }
}

# -------------------------------
# DIRECTORIES
# -------------------------------
$dirs = @(
    "src/main/java/com/svrmslk/booking/config",
    "src/main/java/com/svrmslk/booking/health",
    "src/main/java/com/svrmslk/booking/shared/security",
    "src/main/java/com/svrmslk/booking/shared/domain",
    "src/main/java/com/svrmslk/booking/shared/api",
    "src/main/java/com/svrmslk/booking/shared/exception",
    "src/main/java/com/svrmslk/booking/shared/event",
    "src/main/java/com/svrmslk/booking/domain",
    "src/main/java/com/svrmslk/booking/application/command",
    "src/main/java/com/svrmslk/booking/application",
    "src/main/java/com/svrmslk/booking/infrastructure/persistence",
    "src/main/java/com/svrmslk/booking/infrastructure/event",
    "src/main/java/com/svrmslk/booking/presentation/dto",
    "src/main/java/com/svrmslk/booking/presentation/mapper",
    "src/main/java/com/svrmslk/booking/presentation",
    "src/main/java/com/svrmslk/booking/dashboard/application",
    "src/main/java/com/svrmslk/booking/dashboard/domain",
    "src/main/java/com/svrmslk/booking/dashboard/infrastructure",
    "src/main/java/com/svrmslk/booking/dashboard/presentation/dto",
    "src/main/java/com/svrmslk/booking/dashboard/presentation/mapper",
    "src/main/java/com/svrmslk/booking/dashboard/presentation",
    "src/main/resources/db/migration",
    "src/test/java/com/svrmslk/booking/domain",
    "src/test/java/com/svrmslk/booking/application",
    "src/test/java/com/svrmslk/booking/presentation",
    "src/test/java/com/svrmslk/booking/dashboard",
    "src/test/resources"
)

foreach ($dir in $dirs) {
    New-Dir "$basePath\$dir"
}

# -------------------------------
# FILES
# -------------------------------
$files = @(
    "pom.xml",
    "src/main/java/com/svrmslk/booking/BookingServiceApplication.java",

    # Configs
    "src/main/java/com/svrmslk/booking/config/DatabaseConfig.java",
    "src/main/java/com/svrmslk/booking/config/KafkaConfig.java",
    "src/main/java/com/svrmslk/booking/config/ObservabilityConfig.java",
    "src/main/java/com/svrmslk/booking/config/SecurityConfig.java",
    "src/main/java/com/svrmslk/booking/config/SwaggerConfig.java",
    "src/main/java/com/svrmslk/booking/config/WebConfig.java",

    # Health
    "src/main/java/com/svrmslk/booking/health/HealthController.java",

    # Shared security
    "src/main/java/com/svrmslk/booking/shared/security/UserContext.java",
    "src/main/java/com/svrmslk/booking/shared/security/UserContextFilter.java",
    "src/main/java/com/svrmslk/booking/shared/security/BookingSecurityContext.java",

    # Shared domain
    "src/main/java/com/svrmslk/booking/shared/domain/BookingId.java",
    "src/main/java/com/svrmslk/booking/shared/domain/CustomerId.java",
    "src/main/java/com/svrmslk/booking/shared/domain/VehicleId.java",
    "src/main/java/com/svrmslk/booking/shared/domain/CompanyId.java",

    # Shared api
    "src/main/java/com/svrmslk/booking/shared/api/ApiResponse.java",
    "src/main/java/com/svrmslk/booking/shared/api/PageResponse.java",

    # Shared exception
    "src/main/java/com/svrmslk/booking/shared/exception/DomainException.java",
    "src/main/java/com/svrmslk/booking/shared/exception/BookingNotFoundException.java",
    "src/main/java/com/svrmslk/booking/shared/exception/ValidationException.java",
    "src/main/java/com/svrmslk/booking/shared/exception/UnauthorizedException.java",
    "src/main/java/com/svrmslk/booking/shared/exception/GlobalExceptionHandler.java",

    # Shared event
    "src/main/java/com/svrmslk/booking/shared/event/DomainEvent.java",
    "src/main/java/com/svrmslk/booking/shared/event/EventMetadata.java",
    "src/main/java/com/svrmslk/booking/shared/event/EventPublisher.java",

    # Domain
    "src/main/java/com/svrmslk/booking/domain/Booking.java",
    "src/main/java/com/svrmslk/booking/domain/BookingStatus.java",

    # Application + command
    "src/main/java/com/svrmslk/booking/application/BookingService.java",
    "src/main/java/com/svrmslk/booking/application/command/CreateBookingCommand.java",
    "src/main/java/com/svrmslk/booking/application/command/CancelBookingCommand.java",

    # Infrastructure
    "src/main/java/com/svrmslk/booking/infrastructure/persistence/BookingEntity.java",
    "src/main/java/com/svrmslk/booking/infrastructure/persistence/BookingEntityMapper.java",
    "src/main/java/com/svrmslk/booking/infrastructure/persistence/BookingJpaRepository.java",
    "src/main/java/com/svrmslk/booking/infrastructure/persistence/BookingRepository.java",
    "src/main/java/com/svrmslk/booking/infrastructure/event/BookingCreatedEvent.java",
    "src/main/java/com/svrmslk/booking/infrastructure/event/BookingCancelledEvent.java",

    # Presentation
    "src/main/java/com/svrmslk/booking/presentation/BookingController.java",
    "src/main/java/com/svrmslk/booking/presentation/dto/CreateBookingRequest.java",
    "src/main/java/com/svrmslk/booking/presentation/dto/CancelBookingRequest.java",
    "src/main/java/com/svrmslk/booking/presentation/dto/BookingResponse.java",
    "src/main/java/com/svrmslk/booking/presentation/mapper/BookingDtoMapper.java",

    # Dashboard
    "src/main/java/com/svrmslk/booking/dashboard/application/DashboardService.java",
    "src/main/java/com/svrmslk/booking/dashboard/domain/CustomerDashboard.java",
    "src/main/java/com/svrmslk/booking/dashboard/domain/CompanyDashboard.java",
    "src/main/java/com/svrmslk/booking/dashboard/domain/VehiclePerformance.java",
    "src/main/java/com/svrmslk/booking/dashboard/infrastructure/AnalyticsRepository.java",
    "src/main/java/com/svrmslk/booking/dashboard/presentation/DashboardController.java",
    "src/main/java/com/svrmslk/booking/dashboard/presentation/dto/CustomerDashboardResponse.java",
    "src/main/java/com/svrmslk/booking/dashboard/presentation/dto/CompanyDashboardResponse.java",
    "src/main/java/com/svrmslk/booking/dashboard/presentation/dto/VehiclePerformanceResponse.java",
    "src/main/java/com/svrmslk/booking/dashboard/presentation/mapper/DashboardDtoMapper.java",

    # Resources
    "src/main/resources/application.yml",
    "src/main/resources/application-dev.yml",
    "src/main/resources/application-prod.yml",
    "src/main/resources/application-test.yml",
    "src/main/resources/db/migration/V1__create_bookings_table.sql",
    "src/main/resources/db/migration/V2__add_updated_at_triggers.sql",

    # Tests
    "src/test/java/com/svrmslk/booking/domain/BookingTest.java",
    "src/test/java/com/svrmslk/booking/application/BookingServiceTest.java",
    "src/test/java/com/svrmslk/booking/presentation/BookingControllerTest.java",
    "src/test/java/com/svrmslk/booking/dashboard/DashboardServiceTest.java"
)

foreach ($file in $files) {
    New-File "$basePath\$file"
}

Write-Host "✅ Booking-service structure created successfully in $basePath"
