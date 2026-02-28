// com/svrmslk/notificationservice/common/context/ContextHolder.java
package com.svrmslk.notificationservice.common.context;

import java.util.Optional;

public final class ContextHolder {
    private static final ThreadLocal<TenantContext> tenantContext = new InheritableThreadLocal<>();
    private static final ThreadLocal<CorrelationContext> correlationContext = new InheritableThreadLocal<>();

    private ContextHolder() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static void setTenantContext(TenantContext context) {
        if (context == null) {
            throw new IllegalArgumentException("TenantContext cannot be null");
        }
        tenantContext.set(context);
    }

    public static Optional<TenantContext> getTenantContext() {
        return Optional.ofNullable(tenantContext.get());
    }

    public static String getTenantId() {
        return getTenantContext()
                .map(TenantContext::tenantId)
                .orElseThrow(() -> new IllegalStateException("TenantContext not set"));
    }

    public static void clearTenantContext() {
        tenantContext.remove();
    }

    public static void setCorrelationContext(CorrelationContext context) {
        if (context == null) {
            throw new IllegalArgumentException("CorrelationContext cannot be null");
        }
        correlationContext.set(context);
    }

    public static Optional<CorrelationContext> getCorrelationContext() {
        return Optional.ofNullable(correlationContext.get());
    }

    public static String getCorrelationId() {
        return getCorrelationContext()
                .map(CorrelationContext::correlationId)
                .orElse("UNKNOWN");
    }

    public static void clearCorrelationContext() {
        correlationContext.remove();
    }

    public static void clearAll() {
        clearTenantContext();
        clearCorrelationContext();
    }
}