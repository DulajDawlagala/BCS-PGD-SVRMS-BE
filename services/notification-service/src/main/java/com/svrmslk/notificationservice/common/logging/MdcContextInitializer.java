// com/svrmslk/notificationservice/common/logging/MdcContextInitializer.java
package com.svrmslk.notificationservice.common.logging;

import com.svrmslk.notificationservice.common.context.ContextHolder;
import org.slf4j.MDC;

public final class MdcContextInitializer {
    private static final String TENANT_ID_KEY = "tenantId";
    private static final String CORRELATION_ID_KEY = "correlationId";

    private MdcContextInitializer() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static void initialize(String tenantId, String correlationId) {
        if (tenantId != null && !tenantId.isBlank()) {
            MDC.put(TENANT_ID_KEY, tenantId);
        }
        if (correlationId != null && !correlationId.isBlank()) {
            MDC.put(CORRELATION_ID_KEY, correlationId);
        }
    }

    public static void initializeFromContext() {
        ContextHolder.getTenantContext()
                .ifPresent(ctx -> MDC.put(TENANT_ID_KEY, ctx.tenantId()));

        ContextHolder.getCorrelationContext()
                .ifPresent(ctx -> MDC.put(CORRELATION_ID_KEY, ctx.correlationId()));
    }

    public static void clear() {
        MDC.remove(TENANT_ID_KEY);
        MDC.remove(CORRELATION_ID_KEY);
    }

    public static void clearAll() {
        MDC.clear();
    }
}