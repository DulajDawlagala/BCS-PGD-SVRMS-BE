package com.svrmslk.common.events.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.svrmslk.common.events.api.Event;
import com.svrmslk.common.events.core.EventEnvelope;
import com.svrmslk.common.events.core.EventHeaders;
import com.svrmslk.common.events.core.EventMetadata;
import com.svrmslk.common.events.exception.EventSecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * HMAC-based implementation of EventSigner.
 * Uses HMAC-SHA256 for signing event payloads.
 *
 * @author Platform Team
 * @since 1.0.0
 */
public class HmacEventSigner implements EventSigner, EventVerifier {

    private static final Logger log = LoggerFactory.getLogger(HmacEventSigner.class);

    private static final String ALGORITHM = "HmacSHA256";
    private static final String SIGNATURE_ALGORITHM_VALUE = "HMAC-SHA256";

    private final String secretKey;
    private final ObjectMapper objectMapper;
    private final String signedBy;

    public HmacEventSigner(String secretKey, ObjectMapper objectMapper, String signedBy) {
        if (secretKey == null || secretKey.length() < 32) {
            throw new IllegalArgumentException("Secret key must be at least 32 characters");
        }
        this.secretKey = secretKey;
        this.objectMapper = objectMapper;
        this.signedBy = signedBy;

        log.info("HmacEventSigner initialized: algorithm={}, signedBy={}", ALGORITHM, signedBy);
    }

    @Override
    public <T extends Event> EventEnvelope<T> sign(EventEnvelope<T> envelope) {
        try {
            // Create payload to sign (metadata + payload)
            String payloadToSign = createPayloadToSign(envelope);

            // Generate HMAC signature
            String signature = generateSignature(payloadToSign);

            // Add signature to custom headers
            Map<String, String> customHeaders = new HashMap<>(envelope.metadata().customHeaders());
            customHeaders.put(EventHeaders.SIGNATURE, signature);
            customHeaders.put(EventHeaders.SIGNATURE_ALGORITHM, SIGNATURE_ALGORITHM_VALUE);
            customHeaders.put(EventHeaders.SIGNED_BY, signedBy);

            // Create new metadata with signature headers
            EventMetadata signedMetadata = new EventMetadata(
                    envelope.metadata().eventId(),
                    envelope.metadata().eventType(),
                    envelope.metadata().version(),
                    envelope.metadata().timestamp(),
                    envelope.metadata().tenantId(),
                    envelope.metadata().traceId(),
                    envelope.metadata().correlationId(),
                    envelope.metadata().causationId(),
                    envelope.metadata().userId(),
                    envelope.metadata().source(),
                    customHeaders
            );

            log.debug("Event signed: eventId={}, algorithm={}",
                    envelope.getEventId(), SIGNATURE_ALGORITHM_VALUE);

            return new EventEnvelope<>(signedMetadata, envelope.payload());

        } catch (Exception ex) {
            log.error("Failed to sign event: eventId={}", envelope.getEventId(), ex);
            throw new EventSecurityException(
                    envelope.getEventId(),
                    EventSecurityException.SecurityViolationType.SIGNATURE_INVALID,
                    "Failed to sign event",
                    ex
            );
        }
    }

    @Override
    public boolean verify(EventEnvelope<?> envelope) {
        try {
            String expectedSignature = envelope.metadata().customHeaders().get(EventHeaders.SIGNATURE);

            if (expectedSignature == null) {
                log.warn("Event has no signature: eventId={}", envelope.getEventId());
                return false;
            }

            String algorithm = envelope.metadata().customHeaders().get(EventHeaders.SIGNATURE_ALGORITHM);
            if (!SIGNATURE_ALGORITHM_VALUE.equals(algorithm)) {
                log.warn("Unsupported signature algorithm: eventId={}, algorithm={}",
                        envelope.getEventId(), algorithm);
                return false;
            }

            // Recreate payload to verify
            String payloadToVerify = createPayloadToSign(envelope);

            // Generate signature with same method
            String actualSignature = generateSignature(payloadToVerify);

            // Constant-time comparison to prevent timing attacks
            boolean valid = constantTimeEquals(expectedSignature, actualSignature);

            if (!valid) {
                log.warn("Signature verification failed: eventId={}", envelope.getEventId());
            } else {
                log.debug("Signature verified: eventId={}", envelope.getEventId());
            }

            return valid;

        } catch (Exception ex) {
            log.error("Error during signature verification: eventId={}", envelope.getEventId(), ex);
            return false;
        }
    }

    /**
     * Creates a canonical string representation of the envelope for signing.
     */
    private String createPayloadToSign(EventEnvelope<?> envelope) throws JsonProcessingException {
        // Create a deterministic representation
        Map<String, Object> dataToSign = new HashMap<>();
        dataToSign.put("eventId", envelope.metadata().eventId());
        dataToSign.put("eventType", envelope.metadata().eventType().value());
        dataToSign.put("version", envelope.metadata().version().toString());
        dataToSign.put("timestamp", envelope.metadata().timestamp().toString());
        dataToSign.put("tenantId", envelope.metadata().tenantId());
        dataToSign.put("payload", envelope.payload());

        return objectMapper.writeValueAsString(dataToSign);
    }

    /**
     * Generates HMAC-SHA256 signature.
     */
    private String generateSignature(String data) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance(ALGORITHM);
        SecretKeySpec secretKeySpec = new SecretKeySpec(
                secretKey.getBytes(StandardCharsets.UTF_8),
                ALGORITHM
        );
        mac.init(secretKeySpec);

        byte[] hmacBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hmacBytes);
    }

    /**
     * Constant-time string comparison to prevent timing attacks.
     */
    private boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null) {
            return a == b;
        }

        if (a.length() != b.length()) {
            return false;
        }

        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }
}