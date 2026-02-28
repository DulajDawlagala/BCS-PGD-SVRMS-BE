package com.svrmslk.authservice.monitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for authentication-specific metrics.
 * Defines counters and gauges for monitoring auth operations.
 */
@Configuration
public class AuthMetricsConfig {

    @Bean
    public Counter loginSuccessCounter(MeterRegistry registry) {
        return Counter.builder("auth.login.success")
                .description("Total number of successful login attempts")
                .tag("service", "auth")
                .register(registry);
    }

    @Bean
    public Counter loginFailureCounter(MeterRegistry registry) {
        return Counter.builder("auth.login.failure")
                .description("Total number of failed login attempts")
                .tag("service", "auth")
                .register(registry);
    }

    @Bean
    public Counter accountLockedCounter(MeterRegistry registry) {
        return Counter.builder("auth.account.locked")
                .description("Total number of account lockouts")
                .tag("service", "auth")
                .register(registry);
    }

    @Bean
    public Counter otpGeneratedCounter(MeterRegistry registry) {
        return Counter.builder("auth.otp.generated")
                .description("Total number of OTP codes generated")
                .tag("service", "auth")
                .register(registry);
    }

    @Bean
    public Counter otpVerifiedCounter(MeterRegistry registry) {
        return Counter.builder("auth.otp.verified")
                .description("Total number of OTP codes successfully verified")
                .tag("service", "auth")
                .register(registry);
    }

    @Bean
    public Counter oauthLoginCounter(MeterRegistry registry) {
        return Counter.builder("auth.oauth.login")
                .description("Total number of OAuth2 logins")
                .tag("service", "auth")
                .register(registry);
    }

    @Bean
    public Counter tokenRefreshCounter(MeterRegistry registry) {
        return Counter.builder("auth.token.refresh")
                .description("Total number of token refresh operations")
                .tag("service", "auth")
                .register(registry);
    }

    @Bean
    public Counter tokenReuseDetectedCounter(MeterRegistry registry) {
        return Counter.builder("auth.token.reuse.detected")
                .description("Total number of token reuse detections (security incidents)")
                .tag("service", "auth")
                .tag("severity", "high")
                .register(registry);
    }

    @Bean
    public Counter passwordChangeCounter(MeterRegistry registry) {
        return Counter.builder("auth.password.change")
                .description("Total number of password changes")
                .tag("service", "auth")
                .register(registry);
    }

    @Bean
    public Counter registrationCounter(MeterRegistry registry) {
        return Counter.builder("auth.registration")
                .description("Total number of user registrations")
                .tag("service", "auth")
                .register(registry);
    }
}