// FILE: shared/security/UserContext.java
package com.svrmslk.customer.shared.security;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UserContext {

    private static final ThreadLocal<String> USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> EMAIL = new ThreadLocal<>();
    private static final ThreadLocal<String> SESSION_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> TENANT_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> ROLES = new ThreadLocal<>();

    public static void setUserId(String userId) {
        USER_ID.set(userId);
    }

    public static String getUserId() {
        return USER_ID.get();
    }

    public static void setEmail(String email) {
        EMAIL.set(email);
    }

    public static String getEmail() {
        return EMAIL.get();
    }

    public static void setSessionId(String sessionId) {
        SESSION_ID.set(sessionId);
    }

    public static String getSessionId() {
        return SESSION_ID.get();
    }

    public static void setTenantId(String tenantId) {
        TENANT_ID.set(tenantId);
    }

    public static String getTenantId() {
        return TENANT_ID.get();
    }

    public static void setRoles(String roles) {
        ROLES.set(roles);
    }

    public static String getRoles() {
        return ROLES.get();
    }

    public static void clear() {
        USER_ID.remove();
        EMAIL.remove();
        SESSION_ID.remove();
        TENANT_ID.remove();
        ROLES.remove();
    }
}