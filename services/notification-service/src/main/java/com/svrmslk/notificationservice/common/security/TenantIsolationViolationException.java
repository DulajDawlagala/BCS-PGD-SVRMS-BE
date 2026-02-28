// com/svrmslk/notificationservice/common/security/TenantIsolationViolationException.java
package com.svrmslk.notificationservice.common.security;

public class TenantIsolationViolationException extends TenantValidationException {
    public TenantIsolationViolationException(String message) {
        super(message);
    }

    public TenantIsolationViolationException(String message, Throwable cause) {
        super(message, cause);
    }
}