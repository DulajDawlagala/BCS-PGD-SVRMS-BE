package com.svrmslk.gateway.route;

import com.svrmslk.gateway.filter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {

    @Value("${services.auth-service.url}")
    private String authServiceUrl;

    @Value("${services.customer-service.url}")
    private String customerServiceUrl;

    @Value("${services.tenant-service.url}")
    private String tenantServiceUrl;

    @Value("${services.notification-service.url}")
    private String notificationServiceUrl;

    @Value("${services.company-service.url}")
    private String companyServiceUrl;

    @Value("${services.booking-service.url}")
    private String bookingServiceUrl;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public RouteConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()

                // 🔓 Auth Service (NO JWT)
                .route("auth-service", r -> r
                        .path("/api/v1/auth/**")
                        .filters(f -> f.stripPrefix(0))
                        .uri(authServiceUrl))

                // 🔒 INTERNAL Company Service (Gateway → Company)
                .route("company-service-internal", r -> r
                        .path("/internal/**")
                        .filters(f -> f.stripPrefix(0))
                        .uri(companyServiceUrl))

                // Customer Service
                .route("customer-service", r -> r
                        .path("/api/v1/customers/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
                                .stripPrefix(0))
                        .uri(customerServiceUrl))

                // Tenant Service
                .route("tenant-service", r -> r
                        .path("/api/v1/tenants/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
                                .stripPrefix(0))
                        .uri(tenantServiceUrl))

                // Notification Service
                .route("notification-service", r -> r
                        .path("/api/v1/notifications/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
                                .stripPrefix(0))
                        .uri(notificationServiceUrl))



                // Booking Service
                .route("booking-service", r -> r
                        .path("/api/v1/bookings/**", "/api/v1/dashboard/**","/api/v1/dashboard/company/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
                                .stripPrefix(0))
                        .uri(bookingServiceUrl))

                // Company Service (external APIs)
                .route("company-service", r -> r
                        .path("/api/v1/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
                                .stripPrefix(0))
                        .uri(companyServiceUrl))

                // Vehicle browsing - NO JWT required (public)
                .route("company-service", r -> r
                        .path("/api/v1/vehicles/available")
                        .filters(f -> f
                                .stripPrefix(0))
                        .uri(companyServiceUrl))

                .build();
    }
}
