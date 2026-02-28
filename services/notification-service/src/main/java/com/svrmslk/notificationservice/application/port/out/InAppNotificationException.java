// com/svrmslk/notificationservice/application/port/out/InAppNotificationException.java
package com.svrmslk.notificationservice.application.port.out;

public class InAppNotificationException extends Exception {
    public InAppNotificationException(String message) {
        super(message);
    }

    public InAppNotificationException(String message, Throwable cause) {
        super(message, cause);
    }
}