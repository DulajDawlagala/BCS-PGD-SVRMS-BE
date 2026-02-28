// com/svrmslk/notificationservice/domain/model/NotificationStatus.java
package com.svrmslk.notificationservice.domain.model;

public enum NotificationStatus {
    PENDING,
    SENT,
    FAILED;

    public boolean canTransitionTo(NotificationStatus target) {
        return switch (this) {
            case PENDING -> target == SENT || target == FAILED;
            case SENT -> false;
            case FAILED -> false;
        };
    }
}