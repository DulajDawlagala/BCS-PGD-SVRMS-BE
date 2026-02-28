// com/svrmslk/notificationservice/application/usecase/InAppNotificationFailedException.java
package com.svrmslk.notificationservice.application.usecase;

public class InAppNotificationFailedException extends RuntimeException {
    public InAppNotificationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}