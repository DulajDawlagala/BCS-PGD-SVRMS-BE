//package com.svrmslk.booking.shared.security;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.Arrays;
//import java.util.Collection;
//import java.util.Collections;
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
//            // Fix: Explicitly type as Collection<GrantedAuthority>
//            Collection<GrantedAuthority> authorities = roles != null && !roles.isEmpty()
//                    ? Arrays.stream(roles.split(","))
//                    .map(r -> new SimpleGrantedAuthority("ROLE_" + r.trim()))
//                    .collect(Collectors.toList())
//                    : Collections.emptyList();
//
//            var auth = new UsernamePasswordAuthenticationToken(email, null, authorities);
//            SecurityContextHolder.getContext().setAuthentication(auth);
//
//            log.debug("User context set: userId={}, email={}, roles={}", userId, email, roles);
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

package com.svrmslk.booking.shared.security;

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
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String userId = normalize(request.getHeader("X-USER-ID"));
        String email = normalize(request.getHeader("X-EMAIL"));
        String sessionId = normalize(request.getHeader("X-SESSION-ID"));
        String tenantId = normalize(request.getHeader("X-TENANT-ID"));
        String roles = normalize(request.getHeader("X-GLOBAL-ROLES"));

        if (userId != null) {

            UserContext.setUserId(userId);
            UserContext.setEmail(email);
            UserContext.setSessionId(sessionId);
            UserContext.setTenantId(tenantId); // REAL null if missing
            UserContext.setRoles(roles);

            Collection<GrantedAuthority> authorities =
                    roles != null
                            ? Arrays.stream(roles.split(","))
                            .map(String::trim)
                            .filter(r -> !r.isEmpty())
                            .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                            .collect(Collectors.toList())
                            : Collections.emptyList();

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(email, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.debug(
                    "User context set: userId={}, email={}, tenantId={}, roles={}",
                    userId, email, tenantId, roles
            );
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            UserContext.clear();
            SecurityContextHolder.clearContext();
        }
    }

    /**
     * Normalizes headers:
     * - null → null
     * - "" → null
     * - "null" → null
     */
    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String v = value.trim();
        return v.isEmpty() || v.equalsIgnoreCase("null") ? null : v;
    }
}