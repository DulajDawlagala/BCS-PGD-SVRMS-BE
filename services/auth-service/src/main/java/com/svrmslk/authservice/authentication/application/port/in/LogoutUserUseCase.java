package com.svrmslk.authservice.authentication.application.port.in;

import com.svrmslk.authservice.authentication.application.command.LogoutUserCommand;

public interface LogoutUserUseCase {

    void logout(LogoutUserCommand command);
}