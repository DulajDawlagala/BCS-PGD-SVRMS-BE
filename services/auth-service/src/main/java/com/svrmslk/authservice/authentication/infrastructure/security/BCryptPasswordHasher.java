package com.svrmslk.authservice.authentication.infrastructure.security;

import com.svrmslk.authservice.authentication.application.port.out.PasswordHasherPort;
import com.svrmslk.authservice.authentication.domain.valueobject.Password;
import com.svrmslk.authservice.authentication.domain.valueobject.PasswordHash;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BCryptPasswordHasher implements PasswordHasherPort {

    private final BCryptPasswordEncoder encoder;

    public BCryptPasswordHasher() {
        this.encoder = new BCryptPasswordEncoder(12);
    }

    @Override
    public PasswordHash hash(Password password) {
        String encoded = encoder.encode(password.getValue());
        return PasswordHash.of(encoded);
    }

    @Override
    public boolean matches(String rawPassword, PasswordHash hash) {
        return encoder.matches(rawPassword, hash.getValue());
    }
}