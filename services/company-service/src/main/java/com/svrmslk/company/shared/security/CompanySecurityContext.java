//package com.svrmslk.company.shared.security;
//
//import org.springframework.stereotype.Component;
//
//import java.util.UUID;
//
//@Component
//public class CompanySecurityContext {
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
//    public boolean isCompanyAdmin() {
//        return hasRole("COMPANY_ADMIN");
//    }
//
//    public boolean isOwner() {
//        return hasRole("OWNER");
//    }
//
//    public boolean isCustomer() {
//        return hasRole("CUSTOMER");
//    }
//}


package com.svrmslk.company.shared.security;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CompanySecurityContext {

    public String getUserIdString() {
        String userId = UserContext.getUserId();
        if (userId == null || userId.isBlank()) {
            throw new RuntimeException("No authenticated user found");
        }
        return userId;
    }

    public UUID getUserId() {
        return UUID.fromString(getUserIdString());
    }

    public String getTenantIdString() {
        return UserContext.getTenantId();
    }

    /**
     * Returns tenantId UUID if present, otherwise returns null safely.
     * Tenant may be null during initial onboarding before tenant assignment.
     *
     * @return UUID or null if tenant not yet assigned
     */
    public UUID getTenantId() {
        String tenantId = UserContext.getTenantId();

        // ✅ Handle null, empty, or blank tenantId gracefully
        if (tenantId == null || tenantId.isBlank()) {
            return null;
        }

        try {
            return UUID.fromString(tenantId);
        } catch (IllegalArgumentException e) {
            // Log but don't crash - tenant assignment might be pending
            return null;
        }
    }

    public String getEmail() {
        return UserContext.getEmail();
    }

    public String getSessionId() {
        return UserContext.getSessionId();
    }

    public String getRoles() {
        String roles = UserContext.getRoles();
        return roles != null ? roles : "";
    }

    public boolean hasRole(String role) {
        return getRoles().contains(role);
    }

    public boolean isCompanyAdmin() {
        return hasRole("COMPANY_ADMIN");
    }

    public boolean isOwner() {
        return hasRole("OWNER");
    }

    public boolean isCustomer() {
        return hasRole("CUSTOMER");
    }
}