package com.svrmslk.authservice.authentication.presentation.controller;

import com.svrmslk.authservice.authentication.application.command.*;
import com.svrmslk.authservice.authentication.application.port.in.*;
import com.svrmslk.authservice.authentication.presentation.dto.*;
import com.svrmslk.authservice.authentication.presentation.mapper.AuthenticationMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for authentication operations.
 * Supports email/password authentication, Google OAuth login,
 * token refresh, password change, and logout.
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(
        name = "Authentication",
        description = "Authentication endpoints for email/password login, Google OAuth, token refresh, and session management"
)
public class AuthenticationController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase loginUserUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final LogoutUserUseCase logoutUserUseCase;
    private final OAuthLoginUseCase oAuthLoginUseCase;
    private final AuthenticationMapper mapper;

    public AuthenticationController(
            RegisterUserUseCase registerUserUseCase,
            LoginUserUseCase loginUserUseCase,
            ChangePasswordUseCase changePasswordUseCase,
            RefreshTokenUseCase refreshTokenUseCase,
            LogoutUserUseCase logoutUserUseCase,
            OAuthLoginUseCase oAuthLoginUseCase,
            AuthenticationMapper mapper
    ) {
        this.registerUserUseCase = registerUserUseCase;
        this.loginUserUseCase = loginUserUseCase;
        this.changePasswordUseCase = changePasswordUseCase;
        this.refreshTokenUseCase = refreshTokenUseCase;
        this.logoutUserUseCase = logoutUserUseCase;
        this.oAuthLoginUseCase = oAuthLoginUseCase;
        this.mapper = mapper;
    }

    // ========================= REGISTER =========================

//    @PostMapping("/register")
//    @Operation(
//            summary = "Register new user",
//            description = "Creates a new user account using email and password."
//    )
//    @ApiResponses({
//            @ApiResponse(
//                    responseCode = "201",
//                    description = "User registered successfully",
//                    content = @Content(schema = @Schema(implementation = UserRegistrationResponse.class))
//            ),
//            @ApiResponse(responseCode = "400", description = "Invalid input or weak password"),
//            @ApiResponse(responseCode = "409", description = "User already exists")
//    })
//    public ResponseEntity<UserRegistrationResponse> register(
//            @Valid @RequestBody RegisterUserRequest request
//    ) {
//        RegisterUserCommand command = mapper.toCommand(request);
//        var result = registerUserUseCase.register(command);
//        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(result));
//    }

    @PostMapping("/register")
    @Operation(
            summary = "Register new user",
            description = "Creates a new user account using email and password and returns access + refresh tokens."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "User registered successfully with tokens",
                    content = @Content(schema = @Schema(implementation = AuthenticationResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input or weak password"),
            @ApiResponse(responseCode = "409", description = "User already exists")
    })
    public ResponseEntity<AuthenticationResponse> register(
            @Valid @RequestBody RegisterUserRequest request,
            @RequestHeader(value = "User-Agent", defaultValue = "") String userAgent,
            @RequestHeader(value = "X-Forwarded-For", defaultValue = "") String ipAddress
    ) {
        // 1️⃣ Register user as before
        RegisterUserCommand command = mapper.toCommand(request);
        registerUserUseCase.register(command);

        // 2️⃣ Login to generate tokens (pass ipAddress and userAgent)
        LoginUserCommand loginCommand = new LoginUserCommand(
                request.email(),
                request.password(),
                ipAddress,
                userAgent
        );
        var authResult = loginUserUseCase.login(loginCommand);

        // 3️⃣ Return tokens
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toResponse(authResult));
    }

    // ========================= LOGIN =========================

    @PostMapping("/login")
    @Operation(
            summary = "Email/password login",
            description = "Authenticates user using email and password and returns JWT tokens."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Login successful",
                    content = @Content(schema = @Schema(implementation = AuthenticationResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "403", description = "Account locked or disabled")
    })
    public ResponseEntity<AuthenticationResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        LoginUserCommand command = mapper.toCommand(request);
        var result = loginUserUseCase.login(command);
        return ResponseEntity.ok(mapper.toResponse(result));
    }

    // ========================= GOOGLE OAUTH =========================

    @PostMapping("/oauth/google")
    @Operation(
            summary = "Google OAuth login",
            description = """
            Authenticates user using a Google ID token.
            The client must obtain the ID token via Google Sign-In and send it to this endpoint.
            All identity data is extracted and verified server-side.
            """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "OAuth login successful",
                    content = @Content(schema = @Schema(implementation = AuthenticationResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid or malformed ID token"),
            @ApiResponse(responseCode = "401", description = "Token verification failed")
    })
    public ResponseEntity<AuthenticationResponse> loginWithGoogle(
            @Valid @RequestBody GoogleLoginRequest request
    ) {
        OAuthLoginCommand command = mapper.toOAuthCommand(request);
        var result = oAuthLoginUseCase.loginWithOAuth(command);
        return ResponseEntity.ok(mapper.toResponse(result));
    }

    // ========================= CHANGE PASSWORD =========================

    @PostMapping("/change-password")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(
            summary = "Change password",
            description = "Changes the authenticated user's password using the current password."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid password or policy violation"),
            @ApiResponse(responseCode = "401", description = "Not authenticated or invalid current password")
    })
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());

        ChangePasswordCommand command = new ChangePasswordCommand(
                userId,
                request.currentPassword(),
                request.newPassword()
        );

        changePasswordUseCase.changePassword(command);
        return ResponseEntity.noContent().build();
    }

    // ========================= REFRESH TOKEN =========================

    @PostMapping("/refresh")
    @Operation(
            summary = "Refresh access token",
            description = "Issues new access and refresh tokens using a valid refresh token."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Token refreshed successfully",
                    content = @Content(schema = @Schema(implementation = AuthenticationResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")
    })
    public ResponseEntity<AuthenticationResponse> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        RefreshTokenCommand command = new RefreshTokenCommand(request.refreshToken());
        var result = refreshTokenUseCase.refreshToken(command);
        return ResponseEntity.ok(mapper.toResponse(result));
    }

    // ========================= LOGOUT =========================

    @PostMapping("/logout")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(
            summary = "Logout user",
            description = "Logs out the authenticated user and invalidates all active sessions."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Logout successful"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<Void> logout(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        String sessionId = jwt.getClaimAsString("sid");

        LogoutUserCommand command = new LogoutUserCommand(userId, sessionId);
        logoutUserUseCase.logout(command);

        return ResponseEntity.noContent().build();
    }
}
