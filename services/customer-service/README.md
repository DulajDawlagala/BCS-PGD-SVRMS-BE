# ==========================================
# FILE: README.md
# ==========================================
# Customer Service

Production-ready customer profile and dashboard management service for SVRMS.

## Architecture

**Domain-Driven Design (DDD-lite)** with clean architecture principles:

```
customer-service/
├── shared/          # Shared kernel (domain, security, events, exceptions, api)
├── profile/         # Customer profile aggregate
│   ├── domain/
│   ├── application/
│   ├── infrastructure/
│   └── presentation/
├── dashboard/       # Read-only dashboard aggregate
└── config/          # Spring configuration
```

## Features

### Profile Management
- ✅ Customer registration
- ✅ Profile CRUD operations
- ✅ Driver's license management
- ✅ Preference management
- ✅ JWT-based authentication

### Dashboard
- ✅ Booking statistics aggregation
- ✅ Read-only view from booking-service
- ✅ Fallback on service unavailability

### Security
- ✅ JWT validation (no password storage)
- ✅ Input validation
- ✅ SQL injection prevention
- ✅ XSS protection
- ✅ CORS configuration
- ✅ Customer-level authorization

### Observability
- ✅ Prometheus metrics
- ✅ Health checks (liveness/readiness)
- ✅ Structured logging
- ✅ OpenAPI documentation

## Prerequisites

- Java 17+
- Maven 3.9+
- PostgreSQL 15+
- Kafka (optional for events)
- Docker (for containerized deployment)

## Quick Start

### Local Development

```bash
# 1. Set environment variables
export DB_USERNAME=svrms
export DB_PASSWORD=password
export JWT_SECRET=your-secret-key

# 2. Run dependencies
docker-compose up -d postgres kafka

# 3. Build
mvn clean install

# 4. Run
mvn spring-boot:run
```

### Docker Deployment

```bash
# Build and run all services
docker-compose up --build

# Access Swagger UI
http://localhost:8082/swagger-ui.html

# Access health check
http://localhost:8082/actuator/health
```

## API Endpoints

### Customer Profile
```
POST   /api/v1/customers           - Create customer
GET    /api/v1/customers/me        - Get current profile
GET    /api/v1/customers/{id}      - Get profile by ID
PUT    /api/v1/customers/me        - Update profile
```

### Dashboard
```
GET    /api/v1/dashboard/me        - Get customer dashboard
```

### Health & Monitoring
```
GET    /actuator/health            - Health check
GET    /actuator/metrics           - Metrics
GET    /actuator/prometheus        - Prometheus metrics
```

## Configuration

Key configuration properties:

```yaml
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/svrms_customer

# Security
security.jwt.secret=your-256-bit-secret

# External Services
services.booking-service.url=http://localhost:8083
```

## Testing

```bash
# Run all tests
mvn test

# Run with coverage
mvn test jacoco:report

# Integration tests
mvn verify
```

## Security

### JWT Authentication
- Tokens issued by auth-service
- Type must be "CUSTOMER"
- Claims: sub (customerId), email, type

### Authorization
- Customers can only access their own data
- Enforced at controller and service level
- Security validation via CustomerSecurityContext

## Domain Events

Published to Kafka:

- `CustomerCreatedEvent` (v1)
- `CustomerProfileUpdatedEvent` (v1)

## Database Schema

**customers**
- customer_id (PK)
- email (unique)
- status
- registered_at
- last_login_at

**customer_profiles**
- id (PK)
- customer_id (FK)
- personal information
- driver's license
- preferences

## Monitoring

Prometheus metrics available at `/actuator/prometheus`:

- HTTP request metrics
- JVM metrics
- Database connection pool metrics
- Custom business metrics

## Production Considerations

### Performance
- Connection pooling (HikariCP)
- JPA batch operations
- Query optimization with indexes

### Security
- JWT signature verification
- Input sanitization
- SQL injection prevention via JPA
- No sensitive data in logs

### Resilience
- Graceful shutdown
- Health probes for K8s
- Feign fallbacks for external services

## License

Proprietary - SVRMS Platform