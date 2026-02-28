// com/svrmslk/notificationservice/common/context/TenantContext.java
package com.svrmslk.notificationservice.common.context;

public record TenantContext(String tenantId) {
    public TenantContext {
        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalArgumentException("TenantId cannot be null or blank");
        }
    }
}