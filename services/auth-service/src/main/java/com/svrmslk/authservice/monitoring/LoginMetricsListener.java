package com.svrmslk.authservice.monitoring;

import io.micrometer.core.instrument.Counter;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Event listener for recording authentication metrics.
 * Listens to domain events and updates Micrometer counters.
 */
@Component
public class LoginMetricsListener {

    private final Counter loginSuccessCounter;
    private final Counter loginFailureCounter;
    private final Counter accountLockedCounter;
    private final Counter otpGeneratedCounter;
    private final Counter otpVerifiedCounter;
    private final Counter oauthLoginCounter;
    private final Counter tokenRefreshCounter;
    private final Counter tokenReuseDetectedCounter;
    private final Counter passwordChangeCounter;
    private final Counter registrationCounter;

    public LoginMetricsListener(
            Counter loginSuccessCounter,
            Counter loginFailureCounter,
            Counter accountLockedCounter,
            Counter otpGeneratedCounter,
            Counter otpVerifiedCounter,
            Counter oauthLoginCounter,
            Counter tokenRefreshCounter,
            Counter tokenReuseDetectedCounter,
            Counter passwordChangeCounter,
            Counter registrationCounter
    ) {
        this.loginSuccessCounter = loginSuccessCounter;
        this.loginFailureCounter = loginFailureCounter;
        this.accountLockedCounter = accountLockedCounter;
        this.otpGeneratedCounter = otpGeneratedCounter;
        this.otpVerifiedCounter = otpVerifiedCounter;
        this.oauthLoginCounter = oauthLoginCounter;
        this.tokenRefreshCounter = tokenRefreshCounter;
        this.tokenReuseDetectedCounter = tokenReuseDetectedCounter;
        this.passwordChangeCounter = passwordChangeCounter;
        this.registrationCounter = registrationCounter;
    }

    /**
     * Note: To fully implement this listener, you would need to:
     * 1. Create Spring Application Events for each auth operation
     * 2. Publish these events from use case services
     * 3. Add @EventListener methods here to handle each event type
     *
     * Example implementation for UserLoggedInEvent:
     *
     * @EventListener
     * public void onUserLoggedIn(UserLoggedInEvent event) {
     *     loginSuccessCounter.increment();
     * }
     *
     * This requires minimal changes to existing use case services
     * to publish Spring ApplicationEvents alongside domain events.
     */
}