package com.svrmslk.notificationservice;

import com.svrmslk.notificationservice.infrastructure.external.email.SendGridClient;
import com.svrmslk.notificationservice.common.exception.ExternalProviderException;

public class TempSendGridTest {
    public static void main(String[] args) {
        SendGridClient client = new SendGridClient(
                System.getenv("SENDGRID_API_KEY"),
                System.getenv("SENDGRID_FROM_EMAIL"),
                System.getenv("SENDGRID_FROM_NAME"),
                10
        );

        try {
            client.sendEmail("", "Test Email", "<p>Helloppz  World!</p>");
            System.out.println("✅ Email sent successfully!");
        } catch (ExternalProviderException e) {
            e.printStackTrace();
        }
    }
}
