package com.svrmslk.gateway.configuration;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class JwtConfig {

    private final SecretKey accessTokenKey;

    public JwtConfig(@Value("${jwt.access-token.secret}") String accessTokenSecret) {
        this.accessTokenKey = Keys.hmacShaKeyFor(accessTokenSecret.getBytes(StandardCharsets.UTF_8));
    }

    public Claims validateAndParseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(accessTokenKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.error("JWT validation failed: {}", e.getMessage());
            throw new RuntimeException("Invalid JWT token", e);
        }
    }

    public boolean isTokenExpired(Claims claims) {
        Date expiration = claims.getExpiration();
        return expiration != null && expiration.before(new Date());
    }

    public String getUserId(Claims claims) {
        return claims.getSubject();
    }

    public String getSessionId(Claims claims) {
        return claims.get("sid", String.class);
    }

    public String getEmail(Claims claims) {
        return claims.get("email", String.class);
    }

    @SuppressWarnings("unchecked")
    public List<String> getGlobalRoles(Claims claims) {
        return claims.get("global_roles", List.class);
    }

    @SuppressWarnings("unchecked")
    public String getTenantId(Claims claims) {
        Map<String, Object> tenantContext = claims.get("tenant_context", Map.class);
        if (tenantContext != null) {
            Object tenantId = tenantContext.get("tenant_id");
            return tenantId != null ? tenantId.toString() : null;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public String getTenantType(Claims claims) {
        Map<String, Object> tenantContext = claims.get("tenant_context", Map.class);
        if (tenantContext != null) {
            Object tenantType = tenantContext.get("tenant_type");
            return tenantType != null ? tenantType.toString() : null;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public String getEffectiveRole(Claims claims) {
        Map<String, Object> tenantContext = claims.get("tenant_context", Map.class);
        if (tenantContext != null) {
            Object effectiveRole = tenantContext.get("effective_role");
            return effectiveRole != null ? effectiveRole.toString() : null;
        }
        return null;
    }
}