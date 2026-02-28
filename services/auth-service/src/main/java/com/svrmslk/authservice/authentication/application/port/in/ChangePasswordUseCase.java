package com.svrmslk.authservice.authentication.application.port.in;

import com.svrmslk.authservice.authentication.application.command.ChangePasswordCommand;

public interface ChangePasswordUseCase {

    void changePassword(ChangePasswordCommand command);
}