package com.svrmslk.authservice.authentication.infrastructure.security;

import com.svrmslk.authservice.authentication.application.port.out.TokenGeneratorPort;
import com.svrmslk.authservice.authentication.domain.model.UserAccount;
import com.svrmslk.authservice.authentication.domain.model.UserRole;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * JWT Token Generator - RFC 7519 Compliant
 *
 * CRITICAL DESIGN DECISIONS:
 *
 * 1. WHY USE setSubject() INSTEAD OF CLAIMS?
 *    - RFC 7519 defines "sub" (subject) as THE principal identifier
 *    - Spring Security OAuth2 Resource Server REQUIRES "sub" for principal resolution
 *    - JwtAuthenticationToken.getName() returns jwt.getSubject()
 *    - @AuthenticationPrincipal Jwt automatically exposes getSubject()
 *    - Controllers can call jwt.getSubject() without custom claim extraction
 *
 * 2. WHY THIS IS PRODUCTION-GRADE:
 *    - No custom authentication converters needed
 *    - No decoder patching required
 *    - Works with Spring Security defaults
 *    - Compatible with any OAuth2 resource server
 *    - Follows industry standards (OIDC, OAuth2, JWT)
 *
 * 3. WHY HS384 WITH 48-BYTE KEYS:
 *    - HS384 = HMAC-SHA384 requires minimum 48 bytes (384 bits)
 *    - More secure than HS256 (256 bits)
 *    - Symmetric signing suitable for single-service deployments
 *    - Fast verification, no PKI overhead
 *
 * TOKEN STRUCTURE:
 * {
 *   "sub": "user-uuid",           ← PRINCIPAL (Spring Security uses this)
 *   "email": "user@example.com",  ← Identity claim
 *   "global_roles": ["CUSTOMER"], ← Authorization claim
 *   "tenant_context": {...},      ← Multi-tenancy claim
 *   "sid": "session-uuid",        ← Session tracking
 *   "iat": 1234567890,            ← Issued at (epoch seconds)
 *   "exp": 1234568790             ← Expires at (epoch seconds)
 * }
 */
@Component
public class JwtTokenGenerator implements TokenGeneratorPort {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenGenerator.class);

    // Key lengths for HMAC algorithms (NIST SP 800-107)
    private static final int HS384_KEY_LENGTH = 56; // 384 bits

    private final SecretKey accessTokenKey;
    private final SecretKey refreshTokenKey;

    @Value("${jwt.access-token.validity-seconds:900}")
    private long accessTokenValidity;

    @Value("${jwt.refresh-token.validity-days:30}")
    private long refreshTokenValidity;

    public JwtTokenGenerator(
            @Value("${jwt.access-token.secret}") String accessTokenSecret,
            @Value("${jwt.refresh-token.secret}") String refreshTokenSecret
    ) {
        logger.info("====== JWT TOKEN GENERATOR INITIALIZATION ======");
        logger.info("Algorithm: HS384 (HMAC-SHA384)");
        logger.info("Access Token Validity: {} seconds", accessTokenValidity);
        logger.info("Refresh Token Validity: {} days", refreshTokenValidity);
        logger.info("Access Secret Length: {} bytes", accessTokenSecret.length());
        logger.info("==============================================");

        // Ensure secrets meet HS384 requirements (48 bytes minimum)
        byte[] accessKeyBytes = ensureKeyLength(
                accessTokenSecret.getBytes(StandardCharsets.UTF_8),
                HS384_KEY_LENGTH
        );
        byte[] refreshKeyBytes = ensureKeyLength(
                refreshTokenSecret.getBytes(StandardCharsets.UTF_8),
                HS384_KEY_LENGTH
        );

        this.accessTokenKey = Keys.hmacShaKeyFor(accessKeyBytes);
        this.refreshTokenKey = Keys.hmacShaKeyFor(refreshKeyBytes);

        logger.info("JWT keys initialized successfully (HS384)");
    }

    @Override
    public String generateAccessToken(UserAccount userAccount, TenantContext tenantContext) {
        Instant now = Instant.now();
        Instant expiration = now.plus(accessTokenValidity, ChronoUnit.SECONDS);

        String userId = userAccount.getId().toString();

        // Build custom claims (NOT including "sub" - that's set separately)
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", userAccount.getEmail().getValue());
        claims.put("global_roles", userAccount.getRoles().stream()
                .map(UserRole::name)
                .collect(Collectors.toList()));

        // Tenant context for multi-tenancy
        Map<String, Object> tenantCtx = new HashMap<>();
        tenantCtx.put("tenant_id", tenantContext.tenantId());
        tenantCtx.put("tenant_type", tenantContext.tenantType());
        tenantCtx.put("effective_role", tenantContext.effectiveRole());
        claims.put("tenant_context", tenantCtx);

        // Session tracking
        claims.put("sid", userAccount.getSessionId());

        // Build JWT with EXPLICIT subject setting
        // CRITICAL: setClaims() MUST come before setSubject()
        // or use addClaims() to avoid overwriting subject
        String token = Jwts.builder()
                .setClaims(claims)                          // Set custom claims first
                .setSubject(userId)                         // THEN set subject (principal)
                .setIssuedAt(Date.from(now))               // iat claim
                .setExpiration(Date.from(expiration))      // exp claim
                .signWith(accessTokenKey, SignatureAlgorithm.HS384)
                .compact();

        // Safe logging (first 20 chars only, never full token)
        logger.info("[JWT-GEN] ACCESS token created | subject={} | email={} | iat={} | exp={} | token_prefix={}",
                userId,
                userAccount.getEmail().getValue(),
                now,
                expiration,
                token.substring(0, Math.min(20, token.length())) + "..."
        );

        return token;
    }

    @Override
    public String generateRefreshToken(UserAccount userAccount) {
        Instant now = Instant.now();
        Instant expiration = now.plus(refreshTokenValidity, ChronoUnit.DAYS);

        String userId = userAccount.getId().toString();

        // Refresh tokens are minimal - only subject + standard claims
        String token = Jwts.builder()
                .setSubject(userId)                         // Principal identifier
                .setIssuedAt(Date.from(now))               // iat claim
                .setExpiration(Date.from(expiration))      // exp claim
                .signWith(refreshTokenKey, SignatureAlgorithm.HS384)
                .compact();

        logger.info("[JWT-GEN] REFRESH token created | subject={} | iat={} | exp={} | token_prefix={}",
                userId,
                now,
                expiration,
                token.substring(0, Math.min(20, token.length())) + "..."
        );

        return token;
    }

    /**
     * Ensures key meets the minimum length requirement for HS384.
     *
     * HS384 requires minimum 48 bytes (384 bits) per NIST SP 800-107.
     * If key is too long: truncate to required length
     * If key is too short: pad with zeros (NOT recommended for production)
     *
     * @param key Original key bytes
     * @param requiredLength Required length in bytes
     * @return Key bytes of exact required length
     */
    private byte[] ensureKeyLength(byte[] key, int requiredLength) {
        if (key.length == requiredLength) {
            return key;
        }

        if (key.length < requiredLength) {
            logger.warn("JWT secret is shorter than recommended {} bytes. Padding with zeros (NOT SECURE FOR PRODUCTION)", requiredLength);
        }

        byte[] result = new byte[requiredLength];
        if (key.length > requiredLength) {
            // Truncate
            System.arraycopy(key, 0, result, 0, requiredLength);
        } else {
            // Pad with zeros (security warning already logged)
            System.arraycopy(key, 0, result, 0, key.length);
        }
        return result;
    }
}