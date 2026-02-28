// FILE: shared/security/UserContextFilter.java
package com.svrmslk.customer.shared.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@Component
public class UserContextFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String userId = request.getHeader("X-USER-ID");
        String email = request.getHeader("X-EMAIL");
        String sessionId = request.getHeader("X-SESSION-ID");
        String tenantId = request.getHeader("X-TENANT-ID");
        String roles = request.getHeader("X-GLOBAL-ROLES");

        if (userId != null && !userId.isEmpty()) {

            UserContext.setUserId(userId);
            UserContext.setEmail(email);
            UserContext.setSessionId(sessionId);
            UserContext.setTenantId(tenantId);
            UserContext.setRoles(roles);

            // Add ROLE_ prefix so @PreAuthorize("hasRole('CUSTOMER')") works
            var authorities = roles != null
                    ? Arrays.stream(roles.split(","))
                    .map(r -> new SimpleGrantedAuthority("ROLE_" + r.trim()))
                    .collect(Collectors.toList())
                    : null;

            var auth = new UsernamePasswordAuthenticationToken(email, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(auth);

            log.debug("User context and security context set: userId={}, email={}, roles={}", userId, email, roles);
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            UserContext.clear();
            SecurityContextHolder.clearContext();
        }
    }
}
