// com/svrmslk/notificationservice/application/policy/RateLimitExceededException.java
package com.svrmslk.notificationservice.application.policy;

public class RateLimitExceededException extends Exception {
    private final String tenantId;
    private final String recipient;

    public RateLimitExceededException(String tenantId, String recipient) {
        super("Rate limit exceeded for tenant: " + tenantId + ", recipient: " + recipient);
        this.tenantId = tenantId;
        this.recipient = recipient;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getRecipient() {
        return recipient;
    }
}