////// FILE: shared/security/JwtAuthenticationFilter.java
////package com.svrmslk.customer.shared.security;
////
////import io.jsonwebtoken.Claims;
////import io.jsonwebtoken.Jwts;
////import io.jsonwebtoken.security.Keys;
////import jakarta.servlet.FilterChain;
////import jakarta.servlet.ServletException;
////import jakarta.servlet.http.HttpServletRequest;
////import jakarta.servlet.http.HttpServletResponse;
////import org.springframework.beans.factory.annotation.Value;
////import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
////import org.springframework.security.core.context.SecurityContextHolder;
////import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
////import org.springframework.stereotype.Component;
////import org.springframework.web.filter.OncePerRequestFilter;
////import java.io.IOException;
////import java.nio.charset.StandardCharsets;
////
////@Component
////public class JwtAuthenticationFilter extends OncePerRequestFilter {
////
////    @Value("${security.jwt.secret}")
////    private String jwtSecret;
////
////    @Override
////    protected void doFilterInternal(HttpServletRequest request,
////                                    HttpServletResponse response,
////                                    FilterChain chain) throws ServletException, IOException {
////        try {
////            String jwt = extractJwtFromRequest(request);
////
////            if (jwt != null && validateToken(jwt)) {
////                Claims claims = parseToken(jwt);
////
////                String customerId = claims.getSubject();
////                String email = claims.get("email", String.class);
////                String type = claims.get("type", String.class);
////
////                // Only allow CUSTOMER type
////                if (!"CUSTOMER".equals(type)) {
////                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
////                    response.getWriter().write("{\"error\":\"Invalid user type\"}");
////                    return;
////                }
////
////                JwtCustomerPrincipal principal = new JwtCustomerPrincipal(customerId, email, type);
////
////                UsernamePasswordAuthenticationToken authentication =
////                        new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
////
////                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
////
////                SecurityContextHolder.getContext().setAuthentication(authentication);
////            }
////        } catch (Exception e) {
////            logger.error("Cannot set customer authentication", e);
////        }
////
////        chain.doFilter(request, response);
////    }
////
////    private String extractJwtFromRequest(HttpServletRequest request) {
////        String bearerToken = request.getHeader("Authorization");
////
////        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
////            return bearerToken.substring(7);
////        }
////
////        return null;
////    }
////
////    private boolean validateToken(String token) {
////        try {
////            Jwts.parserBuilder()
////                    .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
////                    .build()
////                    .parseClaimsJws(token);
////            return true;
////        } catch (Exception e) {
////            logger.error("JWT validation failed", e);
////            return false;
////        }
////    }
////
////    private Claims parseToken(String token) {
////        return Jwts.parserBuilder()
////                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
////                .build()
////                .parseClaimsJws(token)
////                .getBody();
////    }
////}
//// FILE: shared/security/JwtAuthenticationFilter.java
//package com.svrmslk.customer.shared.security;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.security.Keys;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import javax.crypto.SecretKey;
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//import java.util.List;
//
//@Component
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//    private static final int HS384_KEY_LENGTH = 56;
//    private final SecretKey verificationKey;
//
//    public JwtAuthenticationFilter(JwtProperties jwtProperties) {
//
//        String secret = jwtProperties.getAccessToken().getSecret();
//
//        if (secret == null || secret.isBlank()) {
//            throw new IllegalStateException("JWT access-token secret is not configured");
//        }
//
//        // Preserve auth-service padding logic exactly
//        byte[] keyBytes = new byte[HS384_KEY_LENGTH];
//        byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
//        System.arraycopy(secretBytes, 0, keyBytes, 0, Math.min(secretBytes.length, HS384_KEY_LENGTH));
//
//        this.verificationKey = Keys.hmacShaKeyFor(keyBytes);
//    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
//            throws ServletException, IOException {
//
//        String header = request.getHeader("Authorization");
//        if (header == null || !header.startsWith("Bearer ")) {
//            chain.doFilter(request, response);
//            return;
//        }
//
//        String token = header.substring(7);
//
//        try {
//            Claims claims = Jwts.parserBuilder()
//                    .setSigningKey(verificationKey)
//                    .build()
//                    .parseClaimsJws(token)
//                    .getBody();
//
//            String userId = claims.getSubject();
//            String email = claims.get("email", String.class);
//            List<String> roles = claims.get("global_roles", List.class);
//
//            if (userId == null || roles == null || !roles.contains("CUSTOMER")) {
//                sendError(response, "Access Denied: Required roles missing");
//                return;
//            }
//
//            JwtCustomerPrincipal principal = new JwtCustomerPrincipal(userId, email, roles);
//
//            UsernamePasswordAuthenticationToken auth =
//                    new UsernamePasswordAuthenticationToken(
//                            principal,
//                            null,
//                            principal.getAuthorities()
//                    );
//
//            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//            SecurityContextHolder.getContext().setAuthentication(auth);
//
//            chain.doFilter(request, response);
//
//        } catch (Exception e) {
//            logger.error("JWT Validation failed", e);
//            sendError(response, "Invalid Token");
//        }
//    }
//
//    private void sendError(HttpServletResponse response, String message) throws IOException {
//        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//        response.setContentType("application/json");
//        response.getWriter()
//                .write(String.format("{\"success\": false, \"message\": \"%s\"}", message));
//    }
//}
