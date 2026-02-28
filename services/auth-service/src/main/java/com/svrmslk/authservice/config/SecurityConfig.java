package com.svrmslk.authservice.config;

import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

/**
 * Spring Security Configuration - Stateless JWT Authentication
 *
 * ARCHITECTURE:
 * - API-first design (JSON in/out, no HTML)
 * - Stateless authentication (no HTTP sessions)
 * - JWT OAuth2 Resource Server
 * - HS384 symmetric key signing
 *
 * SECURITY PRINCIPLES:
 * 1. No form login, no redirects, no HTTP basic
 * 2. Failed authentication → 401 JSON (never 302 redirect)
 * 3. JWT "sub" claim is THE authenticated principal
 * 4. Spring Security handles principal resolution automatically
 * 5. No custom authentication converters needed
 *
 * JWT PRINCIPAL RESOLUTION:
 * - JwtDecoder extracts "sub" claim automatically
 * - JwtAuthenticationToken.getName() returns jwt.getSubject()
 * - @AuthenticationPrincipal Jwt exposes full token
 * - Controllers access userId via jwt.getSubject()
 *
 * STANDARDS COMPLIANCE:
 * - RFC 7519 (JWT)
 * - RFC 6750 (OAuth 2.0 Bearer Token)
 * - Spring Security OAuth2 Resource Server
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    private static final int HS384_KEY_LENGTH = 56; // 384 bits

    @Value("${jwt.access-token.secret}")
    private String jwtSecret;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        logger.info("====== SECURITY FILTER CHAIN CONFIGURATION ======");
        logger.info("Session Management: STATELESS");
        logger.info("Authentication: JWT (HS384)");
        logger.info("Auth Failure Response: 401 Unauthorized (JSON)");
        logger.info("================================================");

        http
                // Disable state & CSRF (JWT is stateless)
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Disable browser-based authentication mechanisms
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .oauth2Login(oauth -> oauth.disable())

                // Return 401 JSON instead of 302 redirects on auth failure
                .exceptionHandling(ex ->
                        ex.authenticationEntryPoint(
                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))

                // Authorization rules
                .authorizeHttpRequests(auth -> auth

                        // Public authentication endpoints
                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/auth/register",
                                "/api/v1/auth/login",
                                "/api/v1/auth/refresh",
                                "/api/v1/auth/oauth/**"
                        ).permitAll()

                        // Email verification (token-based, no auth required)
                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/auth/email/verify"
                        ).permitAll()

                        // API documentation
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // Health checks (public)
                        .requestMatchers(
                                "/actuator/health",
                                "/actuator/health/**",
                                "/actuator/info"
                        ).permitAll()

                        // Actuator management (admin only)
                        .requestMatchers("/actuator/**")
                        .hasRole("SYSTEM_ADMIN")

                        // Everything else requires JWT authentication
                        .anyRequest().authenticated()
                )

                // OAuth2 Resource Server with JWT (NO custom converters)
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt -> jwt.decoder(jwtDecoder()))
                );

        return http.build();
    }

    /**
     * JWT Decoder for HS384 tokens.
     *
     * CRITICAL IMPLEMENTATION DETAILS:
     *
     * 1. NO CUSTOM CLAIM CONVERTERS
     *    - Spring Security extracts "sub" automatically
     *    - JwtAuthenticationToken uses "sub" as principal
     *    - No patching, no mutations, no workarounds
     *
     * 2. HS384 ALGORITHM
     *    - MacAlgorithm.HS384 specified explicitly
     *    - Must match JwtTokenGenerator algorithm
     *    - Symmetric key (same secret for sign + verify)
     *
     * 3. PRINCIPAL RESOLUTION
     *    - Jwt.getSubject() → user UUID
     *    - JwtAuthenticationToken.getName() → same UUID
     *    - @AuthenticationPrincipal Jwt → full token access
     *
     * 4. VALIDATION
     *    - Signature verification (HS384 HMAC)
     *    - Expiration check (exp claim)
     *    - Not-before check (nbf claim, if present)
     *    - Issuer validation (if configured)
     *
     * @return Configured JWT decoder
     */
    @Bean
    public JwtDecoder jwtDecoder() {

        logger.info("====== JWT DECODER CONFIGURATION ======");
        logger.info("Algorithm: HS384 (HMAC-SHA384)");
        logger.info("Secret Length: {} bytes", jwtSecret.length());

        // Ensure key meets HS384 requirements
        byte[] keyBytes = ensureKeyLength(
                jwtSecret.getBytes(StandardCharsets.UTF_8),
                HS384_KEY_LENGTH
        );

        SecretKey key = Keys.hmacShaKeyFor(keyBytes);

        // Build decoder with HS384 algorithm
        NimbusJwtDecoder decoder = NimbusJwtDecoder
                .withSecretKey(key)
                .macAlgorithm(MacAlgorithm.HS384)
                .build();

        logger.info("JWT Decoder initialized successfully");
        logger.info("Principal source: JWT 'sub' claim (automatic)");
        logger.info("======================================");

        // IMPORTANT: NO setClaimSetConverter()
        // Spring Security handles "sub" → principal mapping automatically
        // Any custom converter would break standard OAuth2 behavior

        return decoder;
    }

    /**
     * Ensures key meets HS384 minimum length requirement.
     * Same logic as JwtTokenGenerator for consistency.
     */
    private byte[] ensureKeyLength(byte[] key, int requiredLength) {
        if (key.length == requiredLength) {
            return key;
        }

        if (key.length < requiredLength) {
            logger.warn("JWT secret is shorter than recommended {} bytes", requiredLength);
        }

        byte[] result = new byte[requiredLength];
        if (key.length > requiredLength) {
            System.arraycopy(key, 0, result, 0, requiredLength);
        } else {
            System.arraycopy(key, 0, result, 0, key.length);
        }
        return result;
    }
}