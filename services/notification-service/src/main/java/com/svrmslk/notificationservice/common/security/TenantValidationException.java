// com/svrmslk/notificationservice/common/security/TenantValidationException.java
package com.svrmslk.notificationservice.common.security;

public class TenantValidationException extends RuntimeException {
    public TenantValidationException(String message) {
        super(message);
    }

    public TenantValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}