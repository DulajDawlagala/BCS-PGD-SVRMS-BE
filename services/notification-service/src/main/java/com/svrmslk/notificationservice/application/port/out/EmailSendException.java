// com/svrmslk/notificationservice/application/port/out/EmailSendException.java
package com.svrmslk.notificationservice.application.port.out;

public class EmailSendException extends Exception {
    public EmailSendException(String message) {
        super(message);
    }

    public EmailSendException(String message, Throwable cause) {
        super(message, cause);
    }
}