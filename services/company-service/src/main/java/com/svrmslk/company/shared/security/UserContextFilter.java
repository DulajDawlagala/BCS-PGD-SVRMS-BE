//package com.svrmslk.company.shared.security;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.Arrays;
//import java.util.stream.Collectors;
//
//@Slf4j
//@Component
//public class UserContextFilter extends OncePerRequestFilter {
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//
//        String userId = request.getHeader("X-USER-ID");
//        String email = request.getHeader("X-EMAIL");
//        String sessionId = request.getHeader("X-SESSION-ID");
//        String tenantId = request.getHeader("X-TENANT-ID");
//        String roles = request.getHeader("X-GLOBAL-ROLES");
//
//        if (userId != null && !userId.isEmpty()) {
//
//            UserContext.setUserId(userId);
//            UserContext.setEmail(email);
//            UserContext.setSessionId(sessionId);
//            UserContext.setTenantId(tenantId);
//            UserContext.setRoles(roles);
//
//            // Add ROLE_ prefix so @PreAuthorize("hasRole('COMPANY_ADMIN')") works
//            var authorities = roles != null
//                    ? Arrays.stream(roles.split(","))
//                    .map(r -> new SimpleGrantedAuthority("ROLE_" + r.trim()))
//                    .collect(Collectors.toList())
//                    : null;
//
//            var auth = new UsernamePasswordAuthenticationToken(email, null, authorities);
//            SecurityContextHolder.getContext().setAuthentication(auth);
//
//            log.debug("User context and security context set: userId={}, email={}, roles={}", userId, email, roles);
//        }
//
//        try {
//            filterChain.doFilter(request, response);
//        } finally {
//            UserContext.clear();
//            SecurityContextHolder.clearContext();
//        }
//    }
//}

package com.svrmslk.company.shared.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Slf4j
@Component
public class UserContextFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String userId    = request.getHeader("X-USER-ID");
        String email     = request.getHeader("X-EMAIL");
        String sessionId = request.getHeader("X-SESSION-ID");
        String tenantId  = request.getHeader("X-TENANT-ID");
        String roles     = request.getHeader("X-GLOBAL-ROLES");

        // ✅ Normalize empty strings to null to avoid UUID parsing issues downstream
        userId    = normalizeHeader(userId);
        email     = normalizeHeader(email);
        sessionId = normalizeHeader(sessionId);
        tenantId  = normalizeHeader(tenantId);   // ← KEY FIX: "" becomes null
        roles     = normalizeHeader(roles);

        if (userId != null) {

            UserContext.setUserId(userId);
            UserContext.setEmail(email);
            UserContext.setSessionId(sessionId);
            UserContext.setTenantId(tenantId);   // can be null — handled safely downstream
            UserContext.setRoles(roles);

            // Add ROLE_ prefix so @PreAuthorize("hasRole('COMPANY_ADMIN')") works
            Collection<GrantedAuthority> authorities = roles != null
                    ? Arrays.stream(roles.split(","))
                    .map(r -> new SimpleGrantedAuthority("ROLE_" + r.trim()))
                    .collect(Collectors.toList())
                    : Collections.emptyList();

            var auth = new UsernamePasswordAuthenticationToken(email, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(auth);

            log.debug("User context set: userId={}, tenantId={}, roles={}", userId, tenantId, roles);
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            UserContext.clear();
            SecurityContextHolder.clearContext();
        }
    }

    /**
     * ✅ Converts empty/blank strings to null so downstream code
     * doesn't need to handle empty string edge cases.
     *
     * @param value header value from request
     * @return trimmed value or null if blank
     */
    private String normalizeHeader(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}