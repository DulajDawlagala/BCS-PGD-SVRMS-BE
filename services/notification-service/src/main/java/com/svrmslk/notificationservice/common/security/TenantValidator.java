// com/svrmslk/notificationservice/common/security/TenantValidator.java
package com.svrmslk.notificationservice.common.security;

import com.svrmslk.notificationservice.common.context.ContextHolder;

public final class TenantValidator {

    private TenantValidator() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static void validateTenantAccess(String requestedTenantId) {
        if (requestedTenantId == null || requestedTenantId.isBlank()) {
            throw new TenantValidationException("Requested tenantId cannot be null or blank");
        }

        String contextTenantId = ContextHolder.getTenantContext()
                .map(ctx -> ctx.tenantId())
                .orElseThrow(() -> new TenantValidationException("No tenant context available"));

        if (!contextTenantId.equals(requestedTenantId)) {
            throw new TenantIsolationViolationException(
                    "Tenant isolation violation: context tenantId '" + contextTenantId +
                            "' does not match requested tenantId '" + requestedTenantId + "'"
            );
        }
    }

    public static boolean isTenantAccessValid(String requestedTenantId) {
        try {
            validateTenantAccess(requestedTenantId);
            return true;
        } catch (TenantValidationException e) {
            return false;
        }
    }
}