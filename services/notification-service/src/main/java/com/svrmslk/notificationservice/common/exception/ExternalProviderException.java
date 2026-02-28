// com/svrmslk/notificationservice/common/exception/ExternalProviderException.java
package com.svrmslk.notificationservice.common.exception;

public class ExternalProviderException extends NotificationException {
    private final String provider;
    private final Integer statusCode;

    public ExternalProviderException(String message, String provider) {
        this(message, null, provider, null);
    }

    public ExternalProviderException(String message, Throwable cause, String provider) {
        this(message, cause, provider, null);
    }

    public ExternalProviderException(String message, Throwable cause, String provider, Integer statusCode) {
        super(message, cause);
        this.provider = provider;
        this.statusCode = statusCode;
    }

    public String getProvider() {
        return provider;
    }

    public Integer getStatusCode() {
        return statusCode;
    }
}