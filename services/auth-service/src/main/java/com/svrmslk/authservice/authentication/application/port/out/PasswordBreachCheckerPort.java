package com.svrmslk.authservice.authentication.application.port.out;

/**
 * Port for checking if passwords have been compromised in data breaches.
 * Implementations should use services like HIBP (Have I Been Pwned).
 */
public interface PasswordBreachCheckerPort {

    boolean isPasswordCompromised(String password);
}