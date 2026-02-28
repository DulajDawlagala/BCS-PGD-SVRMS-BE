package com.svrmslk.authservice.authentication.application.port.in;

import com.svrmslk.authservice.authentication.application.command.RefreshTokenCommand;
import com.svrmslk.authservice.authentication.application.dto.AuthenticationResult;

public interface RefreshTokenUseCase {

    AuthenticationResult refreshToken(RefreshTokenCommand command);
}