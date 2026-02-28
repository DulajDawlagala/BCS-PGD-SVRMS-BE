// com/svrmslk/notificationservice/common/exception/NotificationException.java
package com.svrmslk.notificationservice.common.exception;

public class NotificationException extends RuntimeException {
    private final String errorCode;
    private final String tenantId;

    public NotificationException(String message) {
        this(message, null, null, null);
    }

    public NotificationException(String message, Throwable cause) {
        this(message, cause, null, null);
    }

    public NotificationException(String message, String errorCode, String tenantId) {
        this(message, null, errorCode, tenantId);
    }

    public NotificationException(String message, Throwable cause, String errorCode, String tenantId) {
        super(message, cause);
        this.errorCode = errorCode;
        this.tenantId = tenantId;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getTenantId() {
        return tenantId;
    }
}