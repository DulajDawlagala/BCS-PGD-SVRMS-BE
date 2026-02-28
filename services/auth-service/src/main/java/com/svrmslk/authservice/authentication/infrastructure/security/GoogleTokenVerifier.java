package com.svrmslk.authservice.authentication.infrastructure.security;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.svrmslk.authservice.authentication.application.port.out.GoogleTokenVerifierPort;
import com.svrmslk.authservice.authentication.domain.exception.InvalidCredentialsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;

/**
 * Google ID token verifier implementation.
 *
 * Responsibilities:
 * - Signature verification
 * - Expiration validation
 * - Audience validation
 * - Issuer validation
 * - Email verification validation
 *
 * IMPORTANT:
 * - Returns ONLY trusted tokens
 * - Throws exception on any validation failure
 */
@Component
public class GoogleTokenVerifier implements GoogleTokenVerifierPort {

    private static final Logger log = LoggerFactory.getLogger(GoogleTokenVerifier.class);

    private static final Set<String> VALID_ISSUERS = Set.of(
            "https://accounts.google.com",
            "accounts.google.com"
    );

    private final GoogleIdTokenVerifier verifier;

    public GoogleTokenVerifier(@Value("${google.client-id:}") String clientId) {
        if (clientId == null || clientId.isBlank()) {
            throw new IllegalStateException(
                    "Missing required configuration: google.client-id"
            );
        }

        this.verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance()
        )
                .setAudience(Collections.singletonList(clientId))
                .build();
    }


    @Override
    public VerifiedToken verify(String idTokenString) {
        try {
            GoogleIdToken idToken = verifier.verify(idTokenString);

            if (idToken == null) {
                log.warn("Invalid Google ID token");
                throw new InvalidCredentialsException("Invalid Google ID token");
            }

            GoogleIdToken.Payload payload = idToken.getPayload();

            if (!VALID_ISSUERS.contains(payload.getIssuer())) {
                log.warn("Invalid Google token issuer: {}", payload.getIssuer());
                throw new InvalidCredentialsException("Invalid Google token issuer");
            }

            Boolean emailVerified = payload.getEmailVerified();
            if (emailVerified == null || !emailVerified) {
                log.warn("Google email not verified");
                throw new InvalidCredentialsException("Google email is not verified");
            }

            return new VerifiedToken(
                    payload.getSubject(),
                    payload.getEmail(),
                    true,
                    (String) payload.get("name"),
                    (String) payload.get("picture"),
                    payload.getIssuer(),
                    Set.of(payload.getAudience().toString())
            );


        } catch (InvalidCredentialsException e) {
            throw e;
        } catch (Exception e) {
            log.debug("Google token verification error", e);
            throw new InvalidCredentialsException("Invalid Google ID token");
        }
    }
}
