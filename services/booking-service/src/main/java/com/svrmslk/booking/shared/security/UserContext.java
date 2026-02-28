package com.svrmslk.booking.shared.security;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UserContext {

    private static final ThreadLocal<String> userId = new ThreadLocal<>();
    private static final ThreadLocal<String> email = new ThreadLocal<>();
    private static final ThreadLocal<String> sessionId = new ThreadLocal<>();
    private static final ThreadLocal<String> tenantId = new ThreadLocal<>();
    private static final ThreadLocal<String> roles = new ThreadLocal<>();

    public static void setUserId(String id) {
        userId.set(id);
    }

    public static String getUserId() {
        return userId.get();
    }

    public static void setEmail(String userEmail) {
        email.set(userEmail);
    }

    public static String getEmail() {
        return email.get();
    }

    public static void setSessionId(String session) {
        sessionId.set(session);
    }

    public static String getSessionId() {
        return sessionId.get();
    }

    public static void setTenantId(String tenant) {
        tenantId.set(tenant);
    }

    public static String getTenantId() {
        return tenantId.get();
    }

    public static void setRoles(String userRoles) {
        roles.set(userRoles);
    }

    public static String getRoles() {
        return roles.get();
    }

    public static void clear() {
        userId.remove();
        email.remove();
        sessionId.remove();
        tenantId.remove();
        roles.remove();
    }
}