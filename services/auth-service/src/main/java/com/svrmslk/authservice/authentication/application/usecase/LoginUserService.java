package com.svrmslk.authservice.authentication.application.usecase;

import com.svrmslk.authservice.authentication.application.command.LoginUserCommand;
import com.svrmslk.authservice.authentication.application.dto.AuthenticationResult;
import com.svrmslk.authservice.authentication.application.port.in.LoginUserUseCase;
import com.svrmslk.authservice.authentication.application.port.out.EventPublisherPort;
import com.svrmslk.authservice.authentication.application.port.out.PasswordHasherPort;
import com.svrmslk.authservice.authentication.application.port.out.RefreshTokenRepositoryPort;
import com.svrmslk.authservice.authentication.application.port.out.TokenGeneratorPort;
import com.svrmslk.authservice.authentication.domain.exception.InvalidCredentialsException;
import com.svrmslk.authservice.authentication.domain.model.UserAccount;
import com.svrmslk.authservice.authentication.domain.policy.AccountLockoutPolicy;
import com.svrmslk.authservice.authentication.domain.repository.UserAccountRepository;
import com.svrmslk.authservice.authentication.domain.valueobject.Email;
import com.svrmslk.authservice.authentication.infrastructure.security.LoginAttemptService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class LoginUserService implements LoginUserUseCase {

    private static final long ACCESS_TOKEN_VALIDITY_SECONDS = 900;
    private static final long REFRESH_TOKEN_VALIDITY_DAYS = 30;

    private final UserAccountRepository userAccountRepository;
    private final PasswordHasherPort passwordHasher;
    private final TokenGeneratorPort tokenGenerator;
    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final EventPublisherPort eventPublisher;
    private final AccountLockoutPolicy lockoutPolicy;
    private final LoginAttemptService loginAttemptService;

    public LoginUserService(
            UserAccountRepository userAccountRepository,
            PasswordHasherPort passwordHasher,
            TokenGeneratorPort tokenGenerator,
            RefreshTokenRepositoryPort refreshTokenRepository,
            EventPublisherPort eventPublisher,
            AccountLockoutPolicy lockoutPolicy,
            LoginAttemptService loginAttemptService
    ) {
        this.userAccountRepository = userAccountRepository;
        this.passwordHasher = passwordHasher;
        this.tokenGenerator = tokenGenerator;
        this.refreshTokenRepository = refreshTokenRepository;
        this.eventPublisher = eventPublisher;
        this.lockoutPolicy = lockoutPolicy;
        this.loginAttemptService = loginAttemptService;
    }

    @Override
    public AuthenticationResult login(LoginUserCommand command) {

        // 1. IP-based brute force protection (infrastructure concern)
        if (loginAttemptService.isIpBlocked(command.ipAddress())) {
            throw new InvalidCredentialsException(
                    "Too many login attempts. Please try again later."
            );
        }

        Email email = Email.of(command.email());

        UserAccount userAccount;

        // 2. Load user safely
        try {
            userAccount = userAccountRepository.findByEmail(email)
                    .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));
        } catch (InvalidCredentialsException ex) {

            loginAttemptService.recordFailedLogin(
                    command.email(),
                    command.ipAddress(),
                    command.userAgent(),
                    "User not found"
            );

            throw ex;
        }

        // 3. Authenticate (domain logic)
        try {
            userAccount.authenticate(
                    command.password(),
                    passwordHasher::matches,
                    lockoutPolicy
            );
        } catch (RuntimeException ex) {

            loginAttemptService.recordFailedLogin(
                    command.email(),
                    command.ipAddress(),
                    command.userAgent(),
                    ex.getMessage()
            );

            throw ex;
        }

        // 4. Persist domain state (failed attempt counters, locks)
        userAccountRepository.save(userAccount);

        // 5. Record SUCCESSFUL login
        loginAttemptService.recordSuccessfulLogin(
                userAccount.getId(),
                userAccount.getEmail().getValue(),
                command.ipAddress(),
                command.userAgent()
        );

        // 6. Generate tokens
        String accessToken = tokenGenerator.generateAccessToken(
                userAccount,
                TokenGeneratorPort.TenantContext.empty()
        );

        String refreshToken = tokenGenerator.generateRefreshToken(userAccount);

        refreshTokenRepository.save(
                new RefreshTokenRepositoryPort.RefreshToken(
                        UUID.randomUUID(),
                        userAccount.getId(),
                        refreshToken,
                        Instant.now().plus(REFRESH_TOKEN_VALIDITY_DAYS, ChronoUnit.DAYS),
                        Instant.now()
                )
        );

        // 7. Publish event
        eventPublisher.publish(new UserLoggedInEvent(
                userAccount.getId().toString(),
                userAccount.getEmail().getValue(),
                userAccount.getSessionId()
        ));

        return new AuthenticationResult(
                accessToken,
                refreshToken,
                ACCESS_TOKEN_VALIDITY_SECONDS
        );
    }
}

