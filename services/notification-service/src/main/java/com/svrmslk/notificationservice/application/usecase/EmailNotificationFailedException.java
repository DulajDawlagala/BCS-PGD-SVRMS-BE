// com/svrmslk/notificationservice/application/usecase/EmailNotificationFailedException.java
package com.svrmslk.notificationservice.application.usecase;

public class EmailNotificationFailedException extends RuntimeException {
    public EmailNotificationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}