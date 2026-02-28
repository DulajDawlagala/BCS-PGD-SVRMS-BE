// com/svrmslk/notificationservice/application/port/out/EmailSenderPort.java
package com.svrmslk.notificationservice.application.port.out;

import com.svrmslk.notificationservice.domain.model.Notification;

public interface EmailSenderPort {
    void sendEmail(Notification notification) throws EmailSendException;
}
