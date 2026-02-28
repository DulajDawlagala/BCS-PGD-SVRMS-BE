////// FILE: shared/security/CustomerSecurityContext.java
////package com.svrmslk.customer.shared.security;
////
////import org.springframework.security.core.Authentication;
////import org.springframework.security.core.context.SecurityContextHolder;
////import org.springframework.stereotype.Component;
////
////@Component
////public class CustomerSecurityContext {
////
////    public JwtCustomerPrincipal getCurrentCustomer() {
////        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
////
////        if (authentication == null || !authentication.isAuthenticated()) {
////            throw new SecurityException("No authenticated customer found");
////        }
////
////        Object principal = authentication.getPrincipal();
////        if (!(principal instanceof JwtCustomerPrincipal)) {
////            throw new SecurityException("Invalid principal type");
////        }
////
////        return (JwtCustomerPrincipal) principal;
////    }
////
////    public String getCurrentCustomerId() {
////        return getCurrentCustomer().getCustomerId();
////    }
////
////    public String getCurrentEmail() {
////        return getCurrentCustomer().getEmail();
////    }
////
////    public void validateCustomerAccess(String requestedCustomerId) {
////        String currentCustomerId = getCurrentCustomerId();
////
////        if (!currentCustomerId.equals(requestedCustomerId)) {
////            throw new SecurityException(
////                    "Customer " + currentCustomerId + " cannot access data for " + requestedCustomerId
////            );
////        }
////    }
////}
//
//// FILE: shared/security/CustomerSecurityContext.java
//package com.svrmslk.customer.shared.security;
//
//import com.svrmslk.customer.profile.application.CustomerProfileService;
//import com.svrmslk.customer.profile.domain.Customer;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//public class CustomerSecurityContext {
//
//    private final CustomerProfileService profileService;
//
//    public JwtCustomerPrincipal getCurrentCustomer() {
//        Authentication authentication =
//                SecurityContextHolder.getContext().getAuthentication();
//
//        if (authentication == null || !authentication.isAuthenticated()) {
//            throw new SecurityException("No authenticated customer found");
//        }
//
//        Object principal = authentication.getPrincipal();
//        if (!(principal instanceof JwtCustomerPrincipal)) {
//            throw new SecurityException("Invalid principal type");
//        }
//
//        return (JwtCustomerPrincipal) principal;
//    }
//
//    // ---------------- AUTH ----------------
//    public String getAuthUserId() {
//        return getCurrentCustomer().getAuthUserId();
//    }
//
//    public String getEmail() {
//        return getCurrentCustomer().getEmail();
//    }
//
//    // ---------------- DOMAIN ----------------
//    public String getCurrentCustomerId() {
//        String authUserId = getAuthUserId();
//
//        Customer customer =
//                profileService.getByAuthUserId(authUserId);
//
//        return customer.getId().getValue();
//    }
//
//    public void validateCustomerAccess(String requestedCustomerId) {
//        String currentCustomerId = getCurrentCustomerId();
//
//        if (!currentCustomerId.equals(requestedCustomerId)) {
//            throw new SecurityException(
//                    "Customer " + currentCustomerId +
//                            " cannot access data for " + requestedCustomerId
//            );
//        }
//    }
//}
//


// FILE: shared/security/CustomerSecurityContext.java
package com.svrmslk.customer.shared.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.Collections;
import java.util.Arrays;

@Slf4j
@Component
public class CustomerSecurityContext {

    /**
     * Get authenticated user ID from gateway headers
     * @return auth user ID from X-USER-ID header
     */
    public String getAuthUserId() {
        String userId = UserContext.getUserId();

        if (userId == null || userId.isEmpty()) {
            log.warn("No user ID found in context - request may not have come through gateway");
            throw new IllegalStateException("User not authenticated - missing X-USER-ID header");
        }

        return userId;
    }

    /**
     * Get authenticated user email from gateway headers
     * @return email from X-EMAIL header
     */
    public String getAuthEmail() {
        String email = UserContext.getEmail();

        if (email == null || email.isEmpty()) {
            log.warn("No email found in context");
            throw new IllegalStateException("User email not available");
        }

        return email;
    }

    /**
     * Get session ID from gateway headers
     * @return session ID from X-SESSION-ID header
     */
    public String getSessionId() {
        return UserContext.getSessionId();
    }

    /**
     * Get tenant ID from gateway headers
     * @return tenant ID from X-TENANT-ID header
     */
    public String getTenantId() {
        return UserContext.getTenantId();
    }

    /**
     * Get user roles from gateway headers
     * @return roles from X-GLOBAL-ROLES header
     */
    public String getRoles() {
        return UserContext.getRoles();
    }

    /**
     * Check if user has a specific role
     * @param role role to check
     * @return true if user has the role
     */
    public boolean hasRole(String role) {
        String roles = UserContext.getRoles();
        return roles != null && roles.contains(role);
    }

    /**
     * Legacy method - kept for backward compatibility
     * @return JwtCustomerPrincipal with data from gateway headers
     */
    @Deprecated
    public JwtCustomerPrincipal getCurrentCustomer() {
        return new JwtCustomerPrincipal(
                getAuthUserId(),
                getAuthEmail(),
                getRoles() != null ? Arrays.asList(getRoles().split(",")) : Collections.emptyList()
        );
    }
}
