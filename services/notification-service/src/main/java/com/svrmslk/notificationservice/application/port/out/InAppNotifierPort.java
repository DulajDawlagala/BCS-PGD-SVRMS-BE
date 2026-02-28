// com/svrmslk/notificationservice/application/port/out/InAppNotifierPort.java
//package com.svrmslk.notificationservice.application.port.out;
//
//import com.svrmslk.notificationservice.domain.model.Notification;
//
//public interface InAppNotifierPort {
//    void sendInAppNotification(Notification notification);
//}

package com.svrmslk.notificationservice.application.port.out;

import com.svrmslk.notificationservice.domain.model.Notification;

public interface InAppNotifierPort {

    void sendInAppNotification(Notification notification)
            throws InAppNotificationException;
}
