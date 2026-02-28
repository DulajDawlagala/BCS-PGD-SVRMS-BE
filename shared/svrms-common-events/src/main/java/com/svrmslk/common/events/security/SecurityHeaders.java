package com.svrmslk.common.events.security;

import java.util.Map;

/**
 * Utility for extracting and validating security-related headers.
 *
 * @author Platform Team
 * @since 1.0.0
 */
public final class SecurityHeaders {

    private SecurityHeaders() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Validates that required security headers are present.
     *
     * @param headers the headers map
     * @return true if all required headers are present
     */
    public static boolean hasRequiredHeaders(Map<String, String> headers) {
        return headers.containsKey(com.svrmslk.common.events.core.EventHeaders.SIGNATURE) &&
                headers.containsKey(com.svrmslk.common.events.core.EventHeaders.SIGNATURE_ALGORITHM);
    }

    /**
     * Extracts the signature from headers.
     */
    public static String getSignature(Map<String, String> headers) {
        return headers.get(com.svrmslk.common.events.core.EventHeaders.SIGNATURE);
    }

    /**
     * Extracts the signature algorithm from headers.
     */
    public static String getSignatureAlgorithm(Map<String, String> headers) {
        return headers.get(com.svrmslk.common.events.core.EventHeaders.SIGNATURE_ALGORITHM);
    }

    /**
     * Extracts who signed the event.
     */
    public static String getSignedBy(Map<String, String> headers) {
        return headers.get(com.svrmslk.common.events.core.EventHeaders.SIGNED_BY);
    }

    /**
     * Validates tenant isolation - ensures event is from expected tenant.
     */
    public static boolean validateTenant(String expectedTenant, String actualTenant) {
        if (expectedTenant == null) {
            return true; // No tenant restriction
        }
        return expectedTenant.equals(actualTenant);
    }
}