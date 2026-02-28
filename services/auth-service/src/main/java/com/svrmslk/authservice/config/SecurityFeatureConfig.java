//package com.svrmslk.authservice.config;
//
//import com.svrmslk.authservice.authentication.application.port.in.*;
//import com.svrmslk.authservice.authentication.application.port.out.*;
//import com.svrmslk.authservice.authentication.application.usecase.*;
//import com.svrmslk.authservice.authentication.domain.policy.PasswordReusePolicy;
//import com.svrmslk.authservice.authentication.domain.policy.RefreshTokenReusePolicy;
//import com.svrmslk.authservice.authentication.domain.policy.SessionLimitPolicy;
//import com.svrmslk.authservice.authentication.domain.repository.UserAccountRepository;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * Configuration for security features and advanced authentication use cases.
// */
//@Configuration
//public class SecurityFeatureConfig {
//
//    @Bean
//    public PasswordReusePolicy passwordReusePolicy() {
//        return new PasswordReusePolicy();
//    }
//
//    @Bean
//    public RefreshTokenReusePolicy refreshTokenReusePolicy() {
//        return new RefreshTokenReusePolicy();
//    }
//
//    @Bean
//    public SessionLimitPolicy sessionLimitPolicy() {
//        return new SessionLimitPolicy();
//    }
//
//    @Bean
//    public OAuthLoginUseCase oAuthLoginUseCase(
//            UserAccountRepository userAccountRepository,
//            ExternalIdentityRepositoryPort externalIdentityRepository,
//            TokenGeneratorPort tokenGenerator,
//            RefreshTokenRepositoryPort refreshTokenRepository,
//            PasswordHasherPort passwordHasher,
//            com.svrmslk.authservice.authentication.domain.policy.PasswordPolicy passwordPolicy,
//            EventPublisherPort eventPublisher
//    ) {
//        return new OAuthLoginService(
//                userAccountRepository,
//                externalIdentityRepository,
//                tokenGenerator,
//                refreshTokenRepository,
//                passwordHasher,
//                passwordPolicy,
//                eventPublisher
//        );
//    }
//
//    @Bean
//    public SendEmailVerificationUseCase sendEmailVerificationUseCase(
//            UserAccountRepository userAccountRepository,
//            EmailVerificationTokenRepositoryPort tokenRepository,
//            EmailSenderPort emailSender
//    ) {
//        return new SendEmailVerificationService(userAccountRepository, tokenRepository, emailSender);
//    }
//
//    @Bean
//    public VerifyEmailUseCase verifyEmailUseCase(
//            EmailVerificationTokenRepositoryPort tokenRepository,
//            EventPublisherPort eventPublisher
//    ) {
//        return new VerifyEmailService(tokenRepository, eventPublisher);
//    }
//
//    @Bean
//    public GenerateOtpUseCase generateOtpUseCase(
//            UserAccountRepository userAccountRepository,
//            OtpTokenRepositoryPort otpTokenRepository,
//            EmailSenderPort emailSender
//    ) {
//        return new GenerateOtpService(userAccountRepository, otpTokenRepository, emailSender);
//    }
//
//    @Bean
//    public VerifyOtpUseCase verifyOtpUseCase(
//            OtpTokenRepositoryPort otpTokenRepository,
//            EventPublisherPort eventPublisher
//    ) {
//        return new VerifyOtpService(otpTokenRepository, eventPublisher);
//    }

package com.svrmslk.authservice.config;

import com.svrmslk.authservice.authentication.application.port.in.*;
import com.svrmslk.authservice.authentication.application.port.out.*;
import com.svrmslk.authservice.authentication.application.usecase.*;
import com.svrmslk.authservice.authentication.domain.policy.PasswordPolicy;
import com.svrmslk.authservice.authentication.domain.policy.PasswordReusePolicy;
import com.svrmslk.authservice.authentication.domain.policy.RefreshTokenReusePolicy;
import com.svrmslk.authservice.authentication.domain.policy.SessionLimitPolicy;
import com.svrmslk.authservice.authentication.domain.repository.UserAccountRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for security features and advanced authentication use cases.
 *
 * This configuration follows clean architecture principles and enables
 * OAuth features only when properly configured.
 */
@Configuration
public class SecurityFeatureConfig {

    /* =======================
     * Domain Policies
     * ======================= */

    @Bean
    public PasswordReusePolicy passwordReusePolicy() {
        return new PasswordReusePolicy();
    }

    @Bean
    public RefreshTokenReusePolicy refreshTokenReusePolicy() {
        return new RefreshTokenReusePolicy();
    }

    @Bean
    public SessionLimitPolicy sessionLimitPolicy() {
        return new SessionLimitPolicy();
    }

    /* =======================
     * OAuth (Google)
     * Enabled only if configured
     * ======================= */

    @Bean
    @ConditionalOnProperty(name = "google.client-id")
    public OAuthLoginUseCase oAuthLoginUseCase(
            UserAccountRepository userAccountRepository,
            ExternalIdentityRepositoryPort externalIdentityRepository,
            TokenGeneratorPort tokenGenerator,
            RefreshTokenRepositoryPort refreshTokenRepository,
            PasswordHasherPort passwordHasher,
            PasswordPolicy passwordPolicy,
            GoogleTokenVerifierPort googleTokenVerifier,
            EventPublisherPort eventPublisher
    ) {
        return new OAuthLoginService(
                userAccountRepository,
                externalIdentityRepository,
                tokenGenerator,
                refreshTokenRepository,
                passwordHasher,
                passwordPolicy,
                googleTokenVerifier,
                eventPublisher
        );
    }

    /* =======================
     * Email Verification
     * ======================= */

    @Bean
    public SendEmailVerificationUseCase sendEmailVerificationUseCase(
            UserAccountRepository userAccountRepository,
            EmailVerificationTokenRepositoryPort tokenRepository,
            EmailSenderPort emailSender
    ) {
        return new SendEmailVerificationService(
                userAccountRepository,
                tokenRepository,
                emailSender
        );
    }

    @Bean
    public VerifyEmailUseCase verifyEmailUseCase(
            EmailVerificationTokenRepositoryPort tokenRepository,
            EventPublisherPort eventPublisher
    ) {
        return new VerifyEmailService(tokenRepository, eventPublisher);
    }

    /* =======================
     * OTP Authentication
     * ======================= */

    @Bean
    public GenerateOtpUseCase generateOtpUseCase(
            UserAccountRepository userAccountRepository,
            OtpTokenRepositoryPort otpTokenRepository,
            EmailSenderPort emailSender
    ) {
        return new GenerateOtpService(
                userAccountRepository,
                otpTokenRepository,
                emailSender
        );
    }

    @Bean
    public VerifyOtpUseCase verifyOtpUseCase(
            OtpTokenRepositoryPort otpTokenRepository,
            EventPublisherPort eventPublisher
    ) {
        return new VerifyOtpService(otpTokenRepository, eventPublisher);
    }
}
