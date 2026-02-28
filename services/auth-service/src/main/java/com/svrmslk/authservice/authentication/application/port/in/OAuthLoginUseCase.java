package com.svrmslk.authservice.authentication.application.port.in;

import com.svrmslk.authservice.authentication.application.command.OAuthLoginCommand;
import com.svrmslk.authservice.authentication.application.dto.AuthenticationResult;

/**
 * Use case for OAuth2-based authentication (Google, etc.).
 */
public interface OAuthLoginUseCase {

    AuthenticationResult loginWithOAuth(OAuthLoginCommand command);
}