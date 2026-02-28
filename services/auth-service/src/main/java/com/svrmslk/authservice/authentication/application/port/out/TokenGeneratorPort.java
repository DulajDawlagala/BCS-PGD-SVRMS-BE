package com.svrmslk.authservice.authentication.application.port.out;

import com.svrmslk.authservice.authentication.domain.model.UserAccount;

public interface TokenGeneratorPort {

    String generateAccessToken(UserAccount userAccount, TenantContext tenantContext);

    String generateRefreshToken(UserAccount userAccount);

    record TenantContext(String tenantId, String tenantType, String effectiveRole) {
        public static TenantContext empty() {
            return new TenantContext(null, null, null);
        }
    }
}