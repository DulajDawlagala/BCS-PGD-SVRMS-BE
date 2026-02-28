// com/svrmslk/notificationservice/common/context/CorrelationContext.java
package com.svrmslk.notificationservice.common.context;

public record CorrelationContext(String correlationId) {
    public CorrelationContext {
        if (correlationId == null || correlationId.isBlank()) {
            throw new IllegalArgumentException("CorrelationId cannot be null or blank");
        }
    }
}