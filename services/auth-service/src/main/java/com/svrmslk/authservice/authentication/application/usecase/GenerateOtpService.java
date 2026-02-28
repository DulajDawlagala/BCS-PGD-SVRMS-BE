package com.svrmslk.authservice.authentication.application.usecase;

import com.svrmslk.authservice.authentication.application.port.in.GenerateOtpUseCase;
import com.svrmslk.authservice.authentication.application.port.out.EmailSenderPort;
import com.svrmslk.authservice.authentication.application.port.out.OtpTokenRepositoryPort;
import com.svrmslk.authservice.authentication.domain.repository.UserAccountRepository;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.UUID;

/**
 * Service for generating OTP tokens.
 * OTPs are hashed before storage for security.
 */
public class GenerateOtpService implements GenerateOtpUseCase {

    private static final long OTP_VALIDITY_MINUTES = 10;
    private static final int OTP_LENGTH = 6;

    private final UserAccountRepository userAccountRepository;
    private final OtpTokenRepositoryPort otpTokenRepository;
    private final EmailSenderPort emailSender;
    private final SecureRandom secureRandom;

    public GenerateOtpService(
            UserAccountRepository userAccountRepository,
            OtpTokenRepositoryPort otpTokenRepository,
            EmailSenderPort emailSender
    ) {
        this.userAccountRepository = userAccountRepository;
        this.otpTokenRepository = otpTokenRepository;
        this.emailSender = emailSender;
        this.secureRandom = new SecureRandom();
    }

    @Override
    public void generateOtp(UUID userId, String otpType) {
        var userAccount = userAccountRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String rawOtp = generateNumericOtp();
        String otpHash = hashOtp(rawOtp);

        OtpTokenRepositoryPort.OtpToken token = new OtpTokenRepositoryPort.OtpToken(
                UUID.randomUUID(),
                userId,
                otpHash,
                otpType,
                Instant.now().plus(OTP_VALIDITY_MINUTES, ChronoUnit.MINUTES),
                Instant.now(),
                null,
                0,
                false
        );

        otpTokenRepository.save(token);
        emailSender.sendOtpEmail(userAccount.getEmail().getValue(), rawOtp);
    }

    private String generateNumericOtp() {
        int otp = secureRandom.nextInt(900000) + 100000;
        return String.valueOf(otp);
    }

    private String hashOtp(String otp) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(otp.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to hash OTP", e);
        }
    }
}