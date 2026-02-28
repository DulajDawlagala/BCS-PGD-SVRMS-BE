package com.svrmslk.authservice.authentication.presentation.controller;

import com.svrmslk.authservice.authentication.application.port.out.UserSessionRepositoryPort;
import com.svrmslk.authservice.authentication.infrastructure.security.SessionManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for session management operations.
 * Handles viewing and terminating user sessions.
 */
@RestController
@RequestMapping("/api/v1/auth/sessions")
@Tag(name = "Sessions", description = "Session management endpoints for viewing and controlling active sessions")
@SecurityRequirement(name = "Bearer Authentication")
public class SessionController {

    private final SessionManagementService sessionManagementService;

    public SessionController(SessionManagementService sessionManagementService) {
        this.sessionManagementService = sessionManagementService;
    }

    @GetMapping
    @Operation(
            summary = "Get active sessions",
            description = "Retrieves all active sessions for the authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sessions retrieved successfully",
                    content = @Content(schema = @Schema(implementation = UserSessionRepositoryPort.UserSession.class))),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<List<UserSessionRepositoryPort.UserSession>> getActiveSessions(
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        List<UserSessionRepositoryPort.UserSession> sessions =
                sessionManagementService.getActiveSessions(userId);
        return ResponseEntity.ok(sessions);
    }

    @DeleteMapping("/{sessionToken}")
    @Operation(
            summary = "Terminate specific session",
            description = "Terminates a specific session by session token."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Session terminated successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<Void> terminateSession(@PathVariable String sessionToken) {
        sessionManagementService.terminateSession(sessionToken);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    @Operation(
            summary = "Terminate all sessions",
            description = "Terminates all active sessions for the authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "All sessions terminated successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<Void> terminateAllSessions(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        sessionManagementService.terminateAllSessions(userId);
        return ResponseEntity.noContent().build();
    }
}