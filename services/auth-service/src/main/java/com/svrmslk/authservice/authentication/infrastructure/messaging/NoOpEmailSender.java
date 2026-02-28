//package com.svrmslk.authservice.authentication.infrastructure.messaging;
//
//import com.svrmslk.authservice.authentication.application.port.out.EmailSenderPort;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.stereotype.Component;
//
///**
// * No-op email sender for development/testing.
// * Logs emails instead of sending them.
// */
//@Component
//@ConditionalOnProperty(prefix = "email", name = "enabled", havingValue = "false", matchIfMissing = true)
//public class NoOpEmailSender implements EmailSenderPort {
//
//    private static final Logger logger = LoggerFactory.getLogger(NoOpEmailSender.class);
//
//    @Override
//    public void sendVerificationEmail(String toEmail, String verificationToken) {
//        logger.info("Email verification token for {}: {}", toEmail, verificationToken);
//    }
//
//    @Override
//    public void sendOtpEmail(String toEmail, String otp) {
//        logger.info("OTP for {}: {}", toEmail, otp);
//    }
//
//    @Override
//    public void sendPasswordResetEmail(String toEmail, String resetToken) {
//        logger.info("Password reset token for {}: {}", toEmail, resetToken);
//    }
//}

package com.svrmslk.authservice.authentication.infrastructure.messaging;

import com.svrmslk.authservice.authentication.application.port.out.EmailSenderPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * No-op email sender for development/testing when Kafka is disabled.
 * Logs emails instead of sending them.
 */
@Component
@ConditionalOnProperty(prefix = "events", name = "enabled", havingValue = "false", matchIfMissing = true)
public class NoOpEmailSender implements EmailSenderPort {

    private static final Logger logger = LoggerFactory.getLogger(NoOpEmailSender.class);

    @Override
    public void sendVerificationEmail(String toEmail, String verificationToken) {
        logger.info("Email verification token for {}: {}", toEmail, verificationToken);
    }

    @Override
    public void sendOtpEmail(String toEmail, String otp) {
        logger.info("OTP for {}: {}", toEmail, otp);
    }

    @Override
    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        logger.info("Password reset token for {}: {}", toEmail, resetToken);
    }
}