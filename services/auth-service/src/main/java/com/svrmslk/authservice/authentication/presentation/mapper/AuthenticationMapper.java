package com.svrmslk.authservice.authentication.presentation.mapper;

import com.svrmslk.authservice.authentication.application.command.LoginUserCommand;
import com.svrmslk.authservice.authentication.application.command.OAuthLoginCommand;
import com.svrmslk.authservice.authentication.application.command.RegisterUserCommand;
import com.svrmslk.authservice.authentication.application.dto.AuthenticationResult;
import com.svrmslk.authservice.authentication.application.dto.UserRegistrationResult;
import com.svrmslk.authservice.authentication.presentation.dto.*;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between presentation DTOs and application commands/results.
 *
 * Security note:
 * - OAuth identity data (email, subject, name) must NEVER be trusted from client input.
 * - Only the OAuth ID token is accepted and verified server-side.
 */
@Component
public class AuthenticationMapper {

    public RegisterUserCommand toCommand(RegisterUserRequest request) {
        return new RegisterUserCommand(
                request.email(),
                request.password(),
                request.initialRole()
        );
    }

    public LoginUserCommand toCommand(LoginRequest request) {
        return new LoginUserCommand(
                request.email(),
                request.password(),
                request.ipAddress(),
                request.userAgent()
        );
    }

    /**
     * Maps Google OAuth login request to command.
     *
     * IMPORTANT:
     * - Only the ID token is trusted.
     * - All identity attributes are extracted after token verification.
     */
    public OAuthLoginCommand toOAuthCommand(GoogleLoginRequest request) {
        return new OAuthLoginCommand(
                "google",
                null,               // providerUserId (resolved after verification)
                null,               // email (resolved after verification)
                null,               // name (resolved after verification)
                request.idToken(),  // Google ID token (ONLY trusted input)
                null                // refresh token not used in this flow
        );
    }

    public UserRegistrationResponse toResponse(UserRegistrationResult result) {
        return new UserRegistrationResponse(
                result.userId(),
                result.email()
        );
    }

    public AuthenticationResponse toResponse(AuthenticationResult result) {
        return new AuthenticationResponse(
                result.accessToken(),
                result.refreshToken(),
                result.expiresIn()
        );
    }
}
