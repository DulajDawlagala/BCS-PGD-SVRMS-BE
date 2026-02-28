package com.svrmslk.authservice.authentication.infrastructure.security;

import com.svrmslk.authservice.authentication.application.port.out.PasswordBreachCheckerPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Checks passwords against breach databases using k-anonymity.
 * This is a stub implementation - integrate with HIBP API for production.
 */
@Component
public class PasswordBreachChecker implements PasswordBreachCheckerPort {

    private static final Logger logger = LoggerFactory.getLogger(PasswordBreachChecker.class);

    @Override
    public boolean isPasswordCompromised(String password) {
        try {
            String sha1Hash = calculateSHA1(password);

            // TODO: Integrate with Have I Been Pwned API
            // Use k-anonymity: send first 5 chars of hash, receive list of matches
            // Compare full hash locally

            logger.debug("Password breach check performed (stub implementation)");
            return false;
        } catch (Exception e) {
            logger.error("Error checking password breach", e);
            return false;
        }
    }

    private String calculateSHA1(String input) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString().toUpperCase();
    }
}