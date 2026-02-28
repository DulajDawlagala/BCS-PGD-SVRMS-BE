package com.svrmslk.authservice.config;

import com.svrmslk.authservice.authentication.application.port.in.*;
import com.svrmslk.authservice.authentication.application.port.out.*;
import com.svrmslk.authservice.authentication.application.usecase.*;
import com.svrmslk.authservice.authentication.domain.policy.AccountLockoutPolicy;
import com.svrmslk.authservice.authentication.domain.policy.PasswordPolicy;
import com.svrmslk.authservice.authentication.domain.repository.UserAccountRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.svrmslk.authservice.authentication.infrastructure.security.LoginAttemptService;


@Configuration
public class BeanConfig {

    @Bean
    public PasswordPolicy passwordPolicy() {
        return new PasswordPolicy();
    }

    @Bean
    public AccountLockoutPolicy accountLockoutPolicy() {
        return new AccountLockoutPolicy();
    }

    @Bean
    public RegisterUserUseCase registerUserUseCase(
            UserAccountRepository userAccountRepository,
            PasswordHasherPort passwordHasher,
            EventPublisherPort eventPublisher,
            PasswordPolicy passwordPolicy

    ) {
        return new RegisterUserService(userAccountRepository, passwordHasher, eventPublisher, passwordPolicy);
    }

    @Bean
    public LoginUserUseCase loginUserUseCase(
            UserAccountRepository userAccountRepository,
            PasswordHasherPort passwordHasher,
            TokenGeneratorPort tokenGenerator,
            RefreshTokenRepositoryPort refreshTokenRepository,
            EventPublisherPort eventPublisher,
            AccountLockoutPolicy lockoutPolicy,
            LoginAttemptService loginAttemptService
    ) {
        return new LoginUserService(
                userAccountRepository,
                passwordHasher,
                tokenGenerator,
                refreshTokenRepository,
                eventPublisher,
                lockoutPolicy,
                loginAttemptService
        );
    }

    @Bean
    public ChangePasswordUseCase changePasswordUseCase(
            UserAccountRepository userAccountRepository,
            PasswordHasherPort passwordHasher,
            EventPublisherPort eventPublisher,
            PasswordPolicy passwordPolicy
    ) {
        return new ChangePasswordService(userAccountRepository, passwordHasher, eventPublisher, passwordPolicy);
    }

    @Bean
    public RefreshTokenUseCase refreshTokenUseCase(
            RefreshTokenRepositoryPort refreshTokenRepository,
            UserAccountRepository userAccountRepository,
            TokenGeneratorPort tokenGenerator
    ) {
        return new RefreshTokenService(refreshTokenRepository, userAccountRepository, tokenGenerator);
    }

    @Bean
    public LogoutUserUseCase logoutUserUseCase(
            UserAccountRepository userAccountRepository,
            RefreshTokenRepositoryPort refreshTokenRepository,
            EventPublisherPort eventPublisher
    ) {
        return new LogoutUserService(userAccountRepository, refreshTokenRepository, eventPublisher);
    }
}