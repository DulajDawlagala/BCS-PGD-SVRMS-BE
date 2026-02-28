//package com.svrmslk.booking.shared.security;
//
//import org.springframework.stereotype.Component;
//
//import java.util.UUID;
//
//@Component
//public class BookingSecurityContext {
//
//    public String getUserIdString() {
//        String userId = UserContext.getUserId();
//        if (userId == null) {
//            throw new RuntimeException("No authenticated user found");
//        }
//        return userId;
//    }
//
//    public UUID getUserId() {
//        return UUID.fromString(getUserIdString());
//    }
//
//    public String getTenantIdString() {
//        String tenantId = UserContext.getTenantId();
//        if (tenantId == null) {
//            throw new RuntimeException("No tenant context found");
//        }
//        return tenantId;
//    }
//
//    public UUID getTenantId() {
//        return UUID.fromString(getTenantIdString());
//    }
//
//    public String getEmail() {
//        return UserContext.getEmail();
//    }
//
//    public String getSessionId() {
//        return UserContext.getSessionId();
//    }
//
//    public String getRoles() {
//        String roles = UserContext.getRoles();
//        return roles != null ? roles : "";
//    }
//
//    public boolean hasRole(String role) {
//        String roles = getRoles();
//        return roles.contains(role);
//    }
//
//    public boolean isCustomer() {
//        return hasRole("CUSTOMER");
//    }
//
//    public boolean isCompanyAdmin() {
//        return hasRole("COMPANY_ADMIN");
//    }
//}

package com.svrmslk.booking.shared.security;

import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.Arrays;

@Component
public class BookingSecurityContext {

    /* ================= USER ================= */

    public String getUserIdString() {
        String userId = UserContext.getUserId();

        if (userId == null || userId.isBlank() || userId.equalsIgnoreCase("null")) {
            throw new IllegalStateException("No authenticated user found");
        }

        return userId;
    }

    public UUID getUserId() {
        try {
            return UUID.fromString(getUserIdString());
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException("Invalid user ID format", ex);
        }
    }

    /* ================= TENANT ================= */

    public String getTenantIdString() {
        String tenantId = UserContext.getTenantId();

        if (tenantId == null || tenantId.isBlank() || tenantId.equalsIgnoreCase("null")) {
            throw new IllegalStateException("Tenant context is required for this operation");
        }

        return tenantId;
    }

    public UUID getTenantId() {
        try {
            return UUID.fromString(getTenantIdString());
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException("Invalid tenant ID format", ex);
        }
    }

    /**
     * Use this when tenant is OPTIONAL (e.g. CUSTOMER public flows)
     */
    public boolean hasTenant() {
        String tenantId = UserContext.getTenantId();
        return tenantId != null && !tenantId.isBlank() && !tenantId.equalsIgnoreCase("null");
    }

    /* ================= METADATA ================= */

    public String getEmail() {
        return UserContext.getEmail();
    }

    public String getSessionId() {
        return UserContext.getSessionId();
    }

    /* ================= ROLES ================= */

    public String getRoles() {
        String roles = UserContext.getRoles();
        return roles != null && !roles.equalsIgnoreCase("null") ? roles : "";
    }

    public boolean hasRole(String role) {
        return Arrays.stream(getRoles().split(","))
                .map(String::trim)
                .anyMatch(r -> r.equalsIgnoreCase(role));
    }

    public boolean isCustomer() {
        return hasRole("CUSTOMER");
    }

    public boolean isCompanyAdmin() {
        return hasRole("COMPANY_ADMIN");
    }
}