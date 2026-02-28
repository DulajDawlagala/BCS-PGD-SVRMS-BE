package com.svrmslk.authservice.authentication.presentation.controller;

import com.svrmslk.authservice.authentication.application.port.in.SendEmailVerificationUseCase;
import com.svrmslk.authservice.authentication.application.port.in.VerifyEmailUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for email verification operations.
 * Handles sending verification emails and verifying email addresses.
 */
@RestController
@RequestMapping("/api/v1/auth/email")
@Tag(name = "Email Verification", description = "Email verification endpoints for account confirmation")
public class EmailVerificationController {

    private final SendEmailVerificationUseCase sendEmailVerificationUseCase;
    private final VerifyEmailUseCase verifyEmailUseCase;

    public EmailVerificationController(
            SendEmailVerificationUseCase sendEmailVerificationUseCase,
            VerifyEmailUseCase verifyEmailUseCase
    ) {
        this.sendEmailVerificationUseCase = sendEmailVerificationUseCase;
        this.verifyEmailUseCase = verifyEmailUseCase;
    }

    @PostMapping("/send-verification")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Void> sendVerification(@AuthenticationPrincipal Jwt jwt) {
        // 1️⃣ Log JWT information
        System.out.println("[DEBUG] JWT received: " + jwt.getTokenValue());
        System.out.println("[DEBUG] JWT subject (userId): " + jwt.getSubject());
        System.out.println("[DEBUG] JWT claims: " + jwt.getClaims());

        // 2️⃣ Convert to UUID
        UUID userId = UUID.fromString(jwt.getSubject());
        System.out.println("[DEBUG] Parsed userId: " + userId);

        // 3️⃣ Call use case
        System.out.println("[DEBUG] Calling SendEmailVerificationUseCase.sendVerification");
        sendEmailVerificationUseCase.sendVerification(userId);
        System.out.println("[DEBUG] sendVerification call completed successfully");

        // 4️⃣ Return response
        System.out.println("[DEBUG] Returning 200 OK from /send-verification");
        return ResponseEntity.ok().build();
    }


    @PostMapping("/verify")
    @Operation(
            summary = "Verify email address",
            description = "Verifies the user's email address using the token sent via email."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email verified successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid or expired token")
    })
    public ResponseEntity<Void> verifyEmail(@RequestParam String token) {
        verifyEmailUseCase.verifyEmail(token);
        return ResponseEntity.ok().build();
    }
}