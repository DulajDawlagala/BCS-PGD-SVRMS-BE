package com.svrmslk.authservice.authentication.application.port.out;

import com.svrmslk.authservice.authentication.domain.valueobject.Password;
import com.svrmslk.authservice.authentication.domain.valueobject.PasswordHash;

public interface PasswordHasherPort {

    PasswordHash hash(Password password);

    boolean matches(String rawPassword, PasswordHash hash);
}