package com.svrmslk.tenant.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth

                        // ==========================
                        // Swagger / OpenAPI
                        // ==========================
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // ==========================
                        // Actuator
                        // ==========================
                        .requestMatchers(
                                "/actuator/health/**",
                                "/actuator/info"
                        ).permitAll()

                        // ==========================
                        // Public tenant APIs (if any)
                        // ==========================
                        .requestMatchers(HttpMethod.GET, "/api/v1/tenants/public/**")
                        .permitAll()

                        // ==========================
                        // Everything else secured
                        // ==========================
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}
