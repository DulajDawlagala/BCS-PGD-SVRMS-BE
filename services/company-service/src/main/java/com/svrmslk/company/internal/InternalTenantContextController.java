package com.svrmslk.company.internal;

import com.svrmslk.company.organization.domain.CompanyMember;
import com.svrmslk.company.organization.infrastructure.persistence.CompanyMemberRepository;
import com.svrmslk.company.shared.security.CompanySecurityContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
public class InternalTenantContextController {

    private final CompanySecurityContext securityContext;
    private final CompanyMemberRepository companyMemberRepository;

    @GetMapping("/tenant-context")
    public ResponseEntity<TenantContextResponse> resolveTenantContext() {

        UUID userId = securityContext.getUserId();

        // MVP: single active company per user
        CompanyMember member = companyMemberRepository.findByUserId(userId)
                .stream()
                .findFirst()
                .orElse(null);

        if (member == null) {
            log.debug("No company membership found for user {}", userId);
            return ResponseEntity.ok(TenantContextResponse.empty());
        }

        TenantContextResponse response = TenantContextResponse.builder()
                .tenantId(member.getCompanyId().value())
                .tenantType("COMPANY")
                .effectiveRole(member.getRole().name())
                .build();

        log.debug(
                "Resolved tenant context for user {} → tenantId={}, role={}",
                userId,
                response.getTenantId(),
                response.getEffectiveRole()
        );

        return ResponseEntity.ok(response);
    }
}
