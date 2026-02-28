package com.svrmslk.authservice.authentication.application.usecase;

import com.svrmslk.authservice.authentication.application.command.RefreshTokenCommand;
import com.svrmslk.authservice.authentication.application.dto.AuthenticationResult;
import com.svrmslk.authservice.authentication.application.port.in.RefreshTokenUseCase;
import com.svrmslk.authservice.authentication.application.port.out.RefreshTokenRepositoryPort;
import com.svrmslk.authservice.authentication.application.port.out.TokenGeneratorPort;
import com.svrmslk.authservice.authentication.domain.exception.InvalidCredentialsException;
import com.svrmslk.authservice.authentication.domain.model.UserAccount;
import com.svrmslk.authservice.authentication.domain.repository.UserAccountRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class RefreshTokenService implements RefreshTokenUseCase {

    private static final long ACCESS_TOKEN_VALIDITY_SECONDS = 900;
    private static final long REFRESH_TOKEN_VALIDITY_DAYS = 30;

    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final UserAccountRepository userAccountRepository;
    private final TokenGeneratorPort tokenGenerator;

    public RefreshTokenService(
            RefreshTokenRepositoryPort refreshTokenRepository,
            UserAccountRepository userAccountRepository,
            TokenGeneratorPort tokenGenerator
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userAccountRepository = userAccountRepository;
        this.tokenGenerator = tokenGenerator;
    }

    @Override
    public AuthenticationResult refreshToken(RefreshTokenCommand command) {
        RefreshTokenRepositoryPort.RefreshToken storedToken = refreshTokenRepository
                .findByToken(command.refreshToken())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid refresh token"));

        if (storedToken.expiresAt().isBefore(Instant.now())) {
            refreshTokenRepository.deleteByToken(command.refreshToken());
            throw new InvalidCredentialsException("Refresh token expired");
        }

        UserAccount userAccount = userAccountRepository.findById(storedToken.userId())
                .orElseThrow(() -> new InvalidCredentialsException("User not found"));

        refreshTokenRepository.deleteByToken(command.refreshToken());

        String newAccessToken = tokenGenerator.generateAccessToken(
                userAccount,
                TokenGeneratorPort.TenantContext.empty()
        );

        String newRefreshToken = tokenGenerator.generateRefreshToken(userAccount);

        refreshTokenRepository.save(new RefreshTokenRepositoryPort.RefreshToken(
                UUID.randomUUID(),
                userAccount.getId(),
                newRefreshToken,
                Instant.now().plus(REFRESH_TOKEN_VALIDITY_DAYS, ChronoUnit.DAYS),
                Instant.now()
        ));

        return new AuthenticationResult(
                newAccessToken,
                newRefreshToken,
                ACCESS_TOKEN_VALIDITY_SECONDS
        );
    }
}