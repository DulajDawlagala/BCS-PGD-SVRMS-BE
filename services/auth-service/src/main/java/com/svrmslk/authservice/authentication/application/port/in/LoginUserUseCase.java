package com.svrmslk.authservice.authentication.application.port.in;

import com.svrmslk.authservice.authentication.application.command.LoginUserCommand;
import com.svrmslk.authservice.authentication.application.dto.AuthenticationResult;

public interface LoginUserUseCase {

    AuthenticationResult login(LoginUserCommand command);
}