package com.svrmslk.authservice.authentication.application.port.in;

import com.svrmslk.authservice.authentication.application.command.RegisterUserCommand;
import com.svrmslk.authservice.authentication.application.dto.UserRegistrationResult;

public interface RegisterUserUseCase {

    UserRegistrationResult register(RegisterUserCommand command);
}