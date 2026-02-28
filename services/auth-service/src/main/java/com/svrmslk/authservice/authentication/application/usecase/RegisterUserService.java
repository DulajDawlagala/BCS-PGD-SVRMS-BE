package com.svrmslk.authservice.authentication.application.usecase;

import com.svrmslk.authservice.authentication.application.command.RegisterUserCommand;
import com.svrmslk.authservice.authentication.application.dto.UserRegistrationResult;
import com.svrmslk.authservice.authentication.application.port.in.RegisterUserUseCase;
import com.svrmslk.authservice.authentication.application.port.out.EventPublisherPort;
import com.svrmslk.authservice.authentication.application.port.out.PasswordHasherPort;
import com.svrmslk.authservice.authentication.domain.exception.UserAlreadyExistsException;
import com.svrmslk.authservice.authentication.domain.model.UserAccount;
import com.svrmslk.authservice.authentication.domain.model.UserRole;
import com.svrmslk.authservice.authentication.domain.policy.PasswordPolicy;
import com.svrmslk.authservice.authentication.domain.repository.UserAccountRepository;
import com.svrmslk.authservice.authentication.domain.valueobject.Email;
import com.svrmslk.authservice.authentication.domain.valueobject.Password;
import com.svrmslk.authservice.authentication.domain.valueobject.PasswordHash;

public class RegisterUserService implements RegisterUserUseCase {

    private final UserAccountRepository userAccountRepository;
    private final PasswordHasherPort passwordHasher;
    private final EventPublisherPort eventPublisher;
    private final PasswordPolicy passwordPolicy;

    public RegisterUserService(
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
    public UserRegistrationResult register(RegisterUserCommand command) {
        Email email = Email.of(command.email());

        if (userAccountRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("User with email " + email + " already exists");
        }

        Password password = Password.of(command.password(), passwordPolicy);
        PasswordHash passwordHash = passwordHasher.hash(password);

        UserAccount userAccount = UserAccount.create(email, passwordHash);

        if (command.initialRole() != null && !command.initialRole().isBlank()) {
            UserRole role = UserRole.valueOf(command.initialRole().toUpperCase());
            userAccount.assignRole(role);
        }

        UserAccount savedAccount = userAccountRepository.save(userAccount);

        eventPublisher.publish(new UserRegisteredEvent(
                savedAccount.getId().toString(),
                savedAccount.getEmail().getValue()
        ));

        return new UserRegistrationResult(
                savedAccount.getId(),
                savedAccount.getEmail().getValue()
        );
    }

    private record UserRegisteredEvent(String userId, String email) implements EventPublisherPort.DomainEvent {
        @Override
        public String getEventType() {
            return "UserRegistered";
        }

        @Override
        public String getAggregateId() {
            return userId;
        }
    }
}