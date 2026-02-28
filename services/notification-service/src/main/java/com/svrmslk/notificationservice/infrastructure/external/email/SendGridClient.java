//// com/svrmslk/notificationservice/infrastructure/external/email/SendGridClient.java
//package com.svrmslk.notificationservice.infrastructure.external.email;
//
//import com.sendgrid.Method;
//import com.sendgrid.Request;
//import com.sendgrid.Response;
//import com.sendgrid.SendGrid;
//import com.sendgrid.helpers.mail.Mail;
//import com.sendgrid.helpers.mail.objects.Content;
//import com.sendgrid.helpers.mail.objects.Email;
//import com.svrmslk.notificationservice.common.exception.ExternalProviderException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.time.Duration;
//import java.time.Instant;
//import java.util.concurrent.atomic.AtomicInteger;
//import java.util.concurrent.atomic.AtomicReference;
//
//@Component
//public class SendGridClient {
//    private static final Logger logger = LoggerFactory.getLogger(SendGridClient.class);
//    private static final int CIRCUIT_BREAKER_THRESHOLD = 5;
//    private static final Duration CIRCUIT_BREAKER_RESET_DURATION = Duration.ofMinutes(1);
//
//    private final SendGrid sendGrid;
//    private final String fromEmail;
//    private final String fromName;
//    private final int timeoutSeconds;
//
//    private final AtomicInteger failureCount = new AtomicInteger(0);
//    private final AtomicReference<Instant> circuitOpenedAt = new AtomicReference<>();
//
//    public SendGridClient(
//            @Value("${sendgrid.api-key}") String apiKey,
//            @Value("${sendgrid.from-email}") String fromEmail,
//            @Value("${sendgrid.from-name}") String fromName,
//            @Value("${sendgrid.timeout-seconds:10}") int timeoutSeconds
//    ) {
//        this.sendGrid = new SendGrid(apiKey);
//        this.fromEmail = fromEmail;
//        this.fromName = fromName;
//        this.timeoutSeconds = timeoutSeconds;
//    }
//
//    public void sendEmail(String toEmail, String subject, String htmlContent) throws ExternalProviderException {
//        checkCircuitBreaker();
//
//        Email from = new Email(fromEmail, fromName);
//        Email to = new Email(toEmail);
//        Content content = new Content("text/html", htmlContent);
//        Mail mail = new Mail(from, subject, to, content);
//
//        Request request = new Request();
//        request.setMethod(Method.POST);
//        request.setEndpoint("mail/send");
//
//        try {
//            request.setBody(mail.build());
//
//            long startTime = System.currentTimeMillis();
//            Response response = sendGrid.api(request);
//            long duration = System.currentTimeMillis() - startTime;
//
//            if (duration > timeoutSeconds * 1000) {
//                handleFailure();
//                throw new ExternalProviderException(
//                        "SendGrid request exceeded timeout of " + timeoutSeconds + " seconds",
//                        "SENDGRID",
//                        null
//                );
//            }
//
//            int statusCode = response.getStatusCode();
//
//            if (statusCode >= 200 && statusCode < 300) {
//                handleSuccess();
//                logger.info(
//                        "Email sent successfully via SendGrid to recipient: {}, statusCode: {}, duration: {}ms",
//                        maskEmail(toEmail), statusCode, duration
//                );
//            } else {
//                handleFailure();
//                String errorBody = response.getBody();
//                logger.error(
//                        "SendGrid API returned error for recipient: {}, statusCode: {}, body: {}",
//                        maskEmail(toEmail), statusCode, errorBody
//                );
//                throw new ExternalProviderException(
//                        "SendGrid API returned error: " + errorBody,
//                        "SENDGRID",
//                        statusCode
//                );
//            }
//
//        } catch (IOException e) {
//            handleFailure();
//            logger.error("IOException while sending email via SendGrid to recipient: {}", maskEmail(toEmail), e);
//            throw new ExternalProviderException(
//                    "Failed to send email via SendGrid: " + e.getMessage(),
//                    e,
//                    "SENDGRID"
//            );
//        }
//    }
//
//    private void checkCircuitBreaker() throws ExternalProviderException {
//        Instant openedAt = circuitOpenedAt.get();
//        if (openedAt != null) {
//            Duration timeSinceOpened = Duration.between(openedAt, Instant.now());
//            if (timeSinceOpened.compareTo(CIRCUIT_BREAKER_RESET_DURATION) < 0) {
//                throw new ExternalProviderException(
//                        "Circuit breaker is OPEN for SendGrid. Too many recent failures.",
//                        "SENDGRID"
//                );
//            } else {
//                logger.info("Circuit breaker reset attempted for SendGrid");
//                circuitOpenedAt.set(null);
//                failureCount.set(0);
//            }
//        }
//    }
//
//    private void handleSuccess() {
//        failureCount.set(0);
//        circuitOpenedAt.set(null);
//    }
//
//    private void handleFailure() {
//        int failures = failureCount.incrementAndGet();
//        if (failures >= CIRCUIT_BREAKER_THRESHOLD) {
//            Instant now = Instant.now();
//            circuitOpenedAt.set(now);
//            logger.error(
//                    "Circuit breaker OPENED for SendGrid after {} consecutive failures",
//                    failures
//            );
//        }
//    }
//
//    private String maskEmail(String email) {
//        if (email == null || email.isBlank()) {
//            return "UNKNOWN";
//        }
//        int atIndex = email.indexOf('@');
//        if (atIndex <= 0) {
//            return "***";
//        }
//        String localPart = email.substring(0, atIndex);
//        String domain = email.substring(atIndex);
//
//        if (localPart.length() <= 2) {
//            return "**" + domain;
//        }
//        return localPart.charAt(0) + "***" + localPart.charAt(localPart.length() - 1) + domain;
//    }
//}

// com/svrmslk/notificationservice/infrastructure/external/email/SendGridClient.java
package com.svrmslk.notificationservice.infrastructure.external.email;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.svrmslk.notificationservice.common.exception.ExternalProviderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class SendGridClient {

    private static final Logger logger = LoggerFactory.getLogger(SendGridClient.class);

    private static final int CIRCUIT_BREAKER_THRESHOLD = 5;
    private static final Duration CIRCUIT_BREAKER_RESET_DURATION = Duration.ofMinutes(1);

    private final SendGrid sendGrid;
    private final String fromEmail;
    private final String fromName;
    private final int timeoutSeconds;

    private final AtomicInteger failureCount = new AtomicInteger(0);
    private final AtomicReference<Instant> circuitOpenedAt = new AtomicReference<>();

    public SendGridClient(
            @Value("${sendgrid.api-key}") String apiKey,
            @Value("${sendgrid.from-email}") String fromEmail,
            @Value("${sendgrid.from-name}") String fromName,
            @Value("${sendgrid.timeout-seconds:10}") int timeoutSeconds
    ) {
        this.sendGrid = new SendGrid(apiKey);
        this.fromEmail = fromEmail;
        this.fromName = fromName;
        this.timeoutSeconds = timeoutSeconds;
    }

    public void sendEmail(String toEmail, String subject, String htmlContent) throws ExternalProviderException {
        checkCircuitBreaker();

        Email from = new Email(fromEmail, fromName);
        Email to = new Email(toEmail);
        Content content = new Content("text/html", htmlContent);
        Mail mail = new Mail(from, subject, to, content);

        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");

        try {
            request.setBody(mail.build());

            long startTime = System.currentTimeMillis();
            Response response = sendGrid.api(request);
            long duration = System.currentTimeMillis() - startTime;

            if (duration > timeoutSeconds * 1000) {
                handleFailure();
                throw new ExternalProviderException(
                        "SendGrid request exceeded timeout of " + timeoutSeconds + " seconds",
                        null,
                        "SENDGRID",
                        null
                );
            }

            int statusCode = response.getStatusCode();
            if (statusCode >= 200 && statusCode < 300) {
                handleSuccess();
                logger.info(
                        "Email sent successfully via SendGrid to recipient: {}, statusCode: {}, duration: {}ms",
                        maskEmail(toEmail), statusCode, duration
                );
            } else {
                handleFailure();
                String errorBody = response.getBody();
                logger.error(
                        "SendGrid API returned error for recipient: {}, statusCode: {}, body: {}",
                        maskEmail(toEmail), statusCode, errorBody
                );
                throw new ExternalProviderException(
                        "SendGrid API returned error: " + errorBody,
                        null,
                        "SENDGRID",
                        statusCode
                );
            }

        } catch (IOException e) {
            handleFailure();
            logger.error("IOException while sending email via SendGrid to recipient: {}", maskEmail(toEmail), e);
            throw new ExternalProviderException(
                    "Failed to send email via SendGrid: " + e.getMessage(),
                    e,
                    "SENDGRID",
                    null
            );
        }
    }

    private void checkCircuitBreaker() throws ExternalProviderException {
        Instant openedAt = circuitOpenedAt.get();
        if (openedAt != null) {
            Duration timeSinceOpened = Duration.between(openedAt, Instant.now());
            if (timeSinceOpened.compareTo(CIRCUIT_BREAKER_RESET_DURATION) < 0) {
                throw new ExternalProviderException(
                        "Circuit breaker is OPEN for SendGrid. Too many recent failures.",
                        null,
                        "SENDGRID",
                        null
                );
            } else {
                logger.info("Circuit breaker reset attempted for SendGrid");
                circuitOpenedAt.set(null);
                failureCount.set(0);
            }
        }
    }

    private void handleSuccess() {
        failureCount.set(0);
        circuitOpenedAt.set(null);
    }

    private void handleFailure() {
        int failures = failureCount.incrementAndGet();
        if (failures >= CIRCUIT_BREAKER_THRESHOLD) {
            Instant now = Instant.now();
            circuitOpenedAt.set(now);
            logger.error(
                    "Circuit breaker OPENED for SendGrid after {} consecutive failures",
                    failures
            );
        }
    }

    private String maskEmail(String email) {
        if (email == null || email.isBlank()) {
            return "UNKNOWN";
        }
        int atIndex = email.indexOf('@');
        if (atIndex <= 0) {
            return "***";
        }
        String localPart = email.substring(0, atIndex);
        String domain = email.substring(atIndex);

        if (localPart.length() <= 2) {
            return "**" + domain;
        }
        return localPart.charAt(0) + "***" + localPart.charAt(localPart.length() - 1) + domain;
    }
}
