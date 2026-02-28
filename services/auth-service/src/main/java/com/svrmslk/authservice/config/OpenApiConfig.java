package com.svrmslk.authservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI 3.0 configuration for API documentation.
 * Configures Swagger UI with JWT Bearer authentication support.
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI authServiceOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(apiServers())
                .addSecurityItem(securityRequirement())
                .components(securityComponents());
    }

    private Info apiInfo() {
        return new Info()
                .title("Rental Platform Auth Service")
                .description("Multi-tenant SaaS authentication and authorization service with OAuth2, OTP, and session management")
                .version("v1")
                .contact(new Contact()
                        .name("Rental Platform Team")
                        .email("support@svrmslk.com"))
                .license(new License()
                        .name("Proprietary")
                        .url("https://svrmslk.com/license"));
    }

    private List<Server> apiServers() {
        return List.of(
                new Server()
                        .url("http://localhost:" + serverPort)
                        .description("Local development server"),
                new Server()
                        .url("https://api.svrmslk.com")
                        .description("Production server")
        );
    }

    private SecurityRequirement securityRequirement() {
        return new SecurityRequirement().addList("Bearer Authentication");
    }

    private Components securityComponents() {
        return new Components()
                .addSecuritySchemes("Bearer Authentication",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Enter JWT token obtained from login endpoint"));
    }
}