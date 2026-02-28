package com.svrmslk.authservice.authentication.application.usecase;

import com.svrmslk.authservice.authentication.application.command.OAuthLoginCommand;
import com.svrmslk.authservice.authentication.application.dto.AuthenticationResult;
import com.svrmslk.authservice.authentication.application.port.in.OAuthLoginUseCase;
import com.svrmslk.authservice.authentication.application.port.out.*;
import com.svrmslk.authservice.authentication.domain.model.UserAccount;
import com.svrmslk.authservice.authentication.domain.model.UserRole;
import com.svrmslk.authservice.authentication.domain.policy.PasswordPolicy;
import com.svrmslk.authservice.authentication.domain.repository.UserAccountRepository;
import com.svrmslk.authservice.authentication.domain.valueobject.Email;
import com.svrmslk.authservice.authentication.domain.valueobject.Password;
import com.svrmslk.authservice.authentication.domain.valueobject.PasswordHash;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * Handles Google OAuth authentication.
 *
 * IMPORTANT:
 * - This service TRUSTS GoogleTokenVerifierPort
 * - All token validation (issuer, audience, email_verified, expiry)
 *   MUST be handled by the verifier implementation
 */
public class OAuthLoginService implements OAuthLoginUseCase {

    private static final long ACCESS_TOKEN_VALIDITY_SECONDS = 900;
    private static final long REFRESH_TOKEN_VALIDITY_DAYS = 30;
    private static final String GOOGLE_PROVIDER = "google";

    private final UserAccountRepository userAccountRepository;
    private final ExternalIdentityRepositoryPort externalIdentityRepository;
    private final TokenGeneratorPort tokenGenerator;
    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final PasswordHasherPort passwordHasher;
    private final PasswordPolicy passwordPolicy;
    private final GoogleTokenVerifierPort googleTokenVerifier;
    private final EventPublisherPort eventPublisher;

    public OAuthLoginService(
            UserAccountRepository userAccountRepository,
            ExternalIdentityRepositoryPort externalIdentityRepository,
            TokenGeneratorPort tokenGenerator,
            RefreshTokenRepositoryPort refreshTokenRepository,
            PasswordHasherPort passwordHasher,
            PasswordPolicy passwordPolicy,
            GoogleTokenVerifierPort googleTokenVerifier,
            EventPublisherPort eventPublisher
    ) {
        this.userAccountRepository = userAccountRepository;
        this.externalIdentityRepository = externalIdentityRepository;
        this.tokenGenerator = tokenGenerator;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordHasher = passwordHasher;
        this.passwordPolicy = passwordPolicy;
        this.googleTokenVerifier = googleTokenVerifier;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public AuthenticationResult loginWithOAuth(OAuthLoginCommand command) {

        // 1. Verify Google ID token (throws if invalid)
        GoogleTokenVerifierPort.VerifiedToken token =
                googleTokenVerifier.verify(command.providerAccessToken());

        // 2. Extract trusted identity information
        String googleUserId = token.subject();
        String email = token.email();
        String name = token.name();

        // 3. Find or create user account
        UserAccount userAccount = externalIdentityRepository
                .findByProviderAndProviderUserId(GOOGLE_PROVIDER, googleUserId)
                .map(identity ->
                        userAccountRepository.findById(identity.userId())
                                .orElseThrow(() -> new IllegalStateException("Linked user not found"))
                )
                .orElseGet(() -> {
                    UserAccount account = findOrCreateUserByEmail(email);
                    createExternalIdentity(account.getId(), googleUserId, email, name);
                    return account;
                });

        // 4. Update external identity metadata (name/email sync)
        externalIdentityRepository.findByProviderAndProviderUserId(GOOGLE_PROVIDER, googleUserId)
                .ifPresent(identity -> updateExternalIdentity(identity, email, name));

        // 5. Generate JWT tokens
        String accessToken = tokenGenerator.generateAccessToken(
                userAccount,
                TokenGeneratorPort.TenantContext.empty()
        );

        String refreshToken = tokenGenerator.generateRefreshToken(userAccount);

        refreshTokenRepository.save(new RefreshTokenRepositoryPort.RefreshToken(
                UUID.randomUUID(),
                userAccount.getId(),
                refreshToken,
                Instant.now().plus(REFRESH_TOKEN_VALIDITY_DAYS, ChronoUnit.DAYS),
                Instant.now()
        ));

        // 6. Publish domain event
        eventPublisher.publish(new OAuthLoginEvent(
                userAccount.getId().toString(),
                GOOGLE_PROVIDER,
                email
        ));

        return new AuthenticationResult(
                accessToken,
                refreshToken,
                ACCESS_TOKEN_VALIDITY_SECONDS
        );
    }

    private UserAccount findOrCreateUserByEmail(String emailStr) {
        Email email = Email.of(emailStr);

        return userAccountRepository.findByEmail(email)
                .orElseGet(() -> createNewUser(email));
    }

    private UserAccount createNewUser(Email email) {
        // OAuth users get a random password (never used)
        String randomPassword = UUID.randomUUID() + "Aa1!";
        Password password = Password.of(randomPassword, passwordPolicy);
        PasswordHash passwordHash = passwordHasher.hash(password);

        UserAccount account = UserAccount.create(email, passwordHash);
        account.assignRole(UserRole.CUSTOMER);

        return userAccountRepository.save(account);
    }

    private void createExternalIdentity(
            UUID userId,
            String providerUserId,
            String email,
            String name
    ) {
        externalIdentityRepository.save(
                new ExternalIdentityRepositoryPort.ExternalIdentity(
                        UUID.randomUUID(),
                        userId,
                        GOOGLE_PROVIDER,
                        providerUserId,
                        email,
                        name,
                        null,
                        null,
                        null,
                        Instant.now(),
                        Instant.now()
                )
        );
    }

    private void updateExternalIdentity(
            ExternalIdentityRepositoryPort.ExternalIdentity existing,
            String email,
            String name
    ) {
        externalIdentityRepository.save(
                new ExternalIdentityRepositoryPort.ExternalIdentity(
                        existing.id(),
                        existing.userId(),
                        existing.provider(),
                        existing.providerUserId(),
                        email,
                        name,
                        null,
                        null,
                        null,
                        existing.linkedAt(),
                        Instant.now()
                )
        );
    }

    private record OAuthLoginEvent(
            String userId,
            String provider,
            String email
    ) implements EventPublisherPort.DomainEvent {

        @Override
        public String getEventType() {
            return "OAuthLoginCompleted";
        }

        @Override
        public String getAggregateId() {
            return userId;
        }
    }
}
