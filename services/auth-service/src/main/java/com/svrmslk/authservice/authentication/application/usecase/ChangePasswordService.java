package com.svrmslk.authservice.authentication.application.usecase;

import com.svrmslk.authservice.authentication.application.command.ChangePasswordCommand;
import com.svrmslk.authservice.authentication.application.port.in.ChangePasswordUseCase;
import com.svrmslk.authservice.authentication.application.port.out.EventPublisherPort;
import com.svrmslk.authservice.authentication.application.port.out.PasswordHasherPort;
import com.svrmslk.authservice.authentication.domain.exception.InvalidCredentialsException;
import com.svrmslk.authservice.authentication.domain.model.UserAccount;
import com.svrmslk.authservice.authentication.domain.policy.PasswordPolicy;
import com.svrmslk.authservice.authentication.domain.repository.UserAccountRepository;
import com.svrmslk.authservice.authentication.domain.valueobject.Password;
import com.svrmslk.authservice.authentication.domain.valueobject.PasswordHash;

import java.util.UUID;

/**
 * Application service for changing user passwords.
 */
public class ChangePasswordService implements ChangePasswordUseCase {

    private final UserAccountRepository userAccountRepository;
    private final PasswordHasherPort passwordHasher;
    private final EventPublisherPort eventPublisher;
    private final PasswordPolicy passwordPolicy;

    public ChangePasswordService(
            UserAccountRepository userAccountRepository,
            PasswordHasherPort passwordHasher,
            EventPublisherPort eventPublisher,
            PasswordPolicy passwordPolicy
    ) {
        this.userAccountRepository = userAccountRepository;
        this.passwordHasher = passwordHasher;
        this.eventPublisher = eventPublisher;
        this.passwordPolicy = passwordPolicy;
    }

    @Override
    public void changePassword(ChangePasswordCommand command) {
        UserAccount user = userAccountRepository.findById(command.userId())
                .orElseThrow(() -> new InvalidCredentialsException("User not found"));

        // Validate current password
        Password currentPassword =
                Password.of(command.currentPassword(), passwordPolicy);

        if (!passwordHasher.matches(
                currentPassword.getValue(),
                user.getPasswordHash()
        )) {
            throw new InvalidCredentialsException("Current password is incorrect");
        }

        // Validate + hash new password
        Password newPassword =
                Password.of(command.newPassword(), passwordPolicy);

        PasswordHash newHash = passwordHasher.hash(newPassword);

        // Update aggregate
        user.changePassword(newHash);

        userAccountRepository.save(user);
    }
}
