package com.svrmslk.authservice.authentication.application.command;

public record OAuthLoginCommand(
        String provider,
        String providerUserId,
        String providerEmail,
        String providerName,
        String providerAccessToken,
        String providerRefreshToken
) {}