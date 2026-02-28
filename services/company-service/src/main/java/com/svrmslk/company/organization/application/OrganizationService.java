//package com.svrmslk.company.organization.application;
//
//import com.svrmslk.company.organization.application.command.CreateCompanyCommand;
//import com.svrmslk.company.organization.application.command.InviteMemberCommand;
//import com.svrmslk.company.organization.application.command.UpdateCompanyCommand;
//import com.svrmslk.company.organization.domain.Company;
//import com.svrmslk.company.organization.domain.CompanyMember;
//import com.svrmslk.company.organization.domain.Invitation;
//import com.svrmslk.company.organization.infrastructure.event.*;
//import com.svrmslk.company.organization.infrastructure.persistence.*;
//import com.svrmslk.company.shared.domain.*;
//import com.svrmslk.company.shared.event.EventMetadata;
//import com.svrmslk.company.shared.event.EventPublisher;
//import com.svrmslk.company.shared.exception.CompanyNotFoundException;
//import com.svrmslk.company.shared.exception.UnauthorizedException;
//import com.svrmslk.company.shared.exception.ValidationException;
//import com.svrmslk.company.shared.security.CompanySecurityContext;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.Instant;
//import java.time.temporal.ChronoUnit;
//import java.util.List;
//import java.util.UUID;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class OrganizationService {
//
//    private final CompanyRepository companyRepository;
//    private final CompanyMemberRepository companyMemberRepository;
//    private final InvitationRepository invitationRepository;
//    private final CompanySecurityContext securityContext;
//    private final EventPublisher eventPublisher;
//
//    @Transactional
//    public Company createCompany(CreateCompanyCommand command) {
//        Company company = Company.builder()
//                .id(CompanyId.generate())
//                .tenantId(securityContext.getTenantId())
//                .type(command.getType())
//                .status(CompanyStatus.ACTIVE)
//                .companyName(command.getCompanyName())
//                .businessType(command.getBusinessType())
//                .taxId(command.getTaxId())
//                .registrationNumber(command.getRegistrationNumber())
//                .companyEmail(command.getCompanyEmail())
//                .companyPhone(command.getCompanyPhone())
//                .firstName(command.getFirstName())
//                .lastName(command.getLastName())
//                .email(command.getEmail())
//                .phone(command.getPhone())
//                .streetAddress(command.getStreetAddress())
//                .city(command.getCity())
//                .zipCode(command.getZipCode())
//                .organizationSize(command.getOrganizationSize())
//                .paymentMethod(command.getPaymentMethod())
//                .createdAt(Instant.now())
//                .updatedAt(Instant.now())
//                .build();
//
//        company.validateIndividualLimits();
//
//        Company savedCompany = companyRepository.save(company);
//
//        // Add creator as COMPANY_ADMIN
//        CompanyMember admin = CompanyMember.builder()
//                .id(UUID.randomUUID())
//                .companyId(savedCompany.getId())
//                .userId(securityContext.getUserId())
//                .role(MemberRole.COMPANY_ADMIN)
//                .joinedAt(Instant.now())
//                .tenantId(securityContext.getTenantId())
//                .build();
//
//        companyMemberRepository.save(admin);
//
//        // Publish event
//        publishCompanyCreatedEvent(savedCompany);
//
//        log.info("Company created: {}", savedCompany.getId());
//        return savedCompany;
//    }
//
//    @Transactional
//    public Company updateCompany(UUID companyId, UpdateCompanyCommand command) {
//        Company company = companyRepository.findById(new CompanyId(companyId))
//                .orElseThrow(() -> new CompanyNotFoundException(companyId));
//
//        verifyAccess(company);
//
//        if (command.getCompanyName() != null) company.setCompanyName(command.getCompanyName());
//        if (command.getBusinessType() != null) company.setBusinessType(command.getBusinessType());
//        if (command.getCompanyEmail() != null) company.setCompanyEmail(command.getCompanyEmail());
//        if (command.getCompanyPhone() != null) company.setCompanyPhone(command.getCompanyPhone());
//        if (command.getFirstName() != null) company.setFirstName(command.getFirstName());
//        if (command.getLastName() != null) company.setLastName(command.getLastName());
//        if (command.getEmail() != null) company.setEmail(command.getEmail());
//        if (command.getPhone() != null) company.setPhone(command.getPhone());
//        if (command.getStreetAddress() != null) company.setStreetAddress(command.getStreetAddress());
//        if (command.getCity() != null) company.setCity(command.getCity());
//        if (command.getZipCode() != null) company.setZipCode(command.getZipCode());
//        if (command.getPaymentMethod() != null) company.setPaymentMethod(command.getPaymentMethod());
//
//        company.setUpdatedAt(Instant.now());
//
//        Company updated = companyRepository.save(company);
//
//        publishCompanyProfileUpdatedEvent(updated);
//
//        return updated;
//    }
//
//    public Company getCompany(UUID companyId) {
//        Company company = companyRepository.findById(new CompanyId(companyId))
//                .orElseThrow(() -> new CompanyNotFoundException(companyId));
//
//        verifyAccess(company);
//        return company;
//    }
//
//    public List<Company> getCompaniesByUser() {
//        UUID userId = securityContext.getUserId();
//        return companyRepository.findByUserId(userId);
//    }
//
//    @Transactional
//    public Invitation inviteMember(InviteMemberCommand command) {
//        Company company = companyRepository.findById(new CompanyId(command.getCompanyId()))
//                .orElseThrow(() -> new CompanyNotFoundException(command.getCompanyId()));
//
//        verifyAdminAccess(company);
//
//        Invitation invitation = Invitation.builder()
//                .id(UUID.randomUUID())
//                .companyId(company.getId())
//                .email(new Email(command.getEmail()))
//                .role(command.getRole())
//                .token(UUID.randomUUID().toString())
//                .expiresAt(Instant.now().plus(7, ChronoUnit.DAYS))
//                .accepted(false)
//                .createdAt(Instant.now())
//                .tenantId(securityContext.getTenantId())
//                .build();
//
//        Invitation saved = invitationRepository.save(invitation);
//
//        publishCompanyAdminInvitedEvent(saved);
//
//        return saved;
//    }
//
//    @Transactional
//    public CompanyMember acceptInvitation(String token, UUID userId) {
//        Invitation invitation = invitationRepository.findByToken(token)
//                .orElseThrow(() -> new ValidationException("Invalid invitation token"));
//
//        if (invitation.isAccepted()) {
//            throw new ValidationException("Invitation already accepted");
//        }
//
//        if (invitation.isExpired()) {
//            throw new ValidationException("Invitation expired");
//        }
//
//        CompanyMember member = CompanyMember.builder()
//                .id(UUID.randomUUID())
//                .companyId(invitation.getCompanyId())
//                .userId(userId)
//                .role(invitation.getRole())
//                .joinedAt(Instant.now())
//                .tenantId(invitation.getTenantId())
//                .build();
//
//        companyMemberRepository.save(member);
//
//        invitation.setAccepted(true);
//        invitationRepository.save(invitation);
//
//        publishCompanyMemberAddedEvent(member);
//
//        return member;
//    }
//
//    public List<CompanyMember> getCompanyMembers(UUID companyId) {
//        Company company = companyRepository.findById(new CompanyId(companyId))
//                .orElseThrow(() -> new CompanyNotFoundException(companyId));
//
//        verifyAccess(company);
//
//        return companyMemberRepository.findByCompanyId(new CompanyId(companyId));
//    }
//
//    private void verifyAccess(Company company) {
//        UUID userId = securityContext.getUserId();
//        boolean isMember = companyMemberRepository.findByCompanyId(company.getId())
//                .stream()
//                .anyMatch(m -> m.getUserId().equals(userId));
//
//        if (!isMember) {
//            throw new UnauthorizedException("User is not a member of this company");
//        }
//    }
//
//    private void verifyAdminAccess(Company company) {
//        UUID userId = securityContext.getUserId();
//        boolean isAdmin = companyMemberRepository.findByCompanyId(company.getId())
//                .stream()
//                .anyMatch(m -> m.getUserId().equals(userId) && m.getRole() == MemberRole.COMPANY_ADMIN);
//
//        if (!isAdmin) {
//            throw new UnauthorizedException("User is not an admin of this company");
//        }
//    }
//
//    private void publishCompanyCreatedEvent(Company company) {
//        CompanyCreatedEvent event = new CompanyCreatedEvent(
//                company.getId().value(),
//                company.getTenantId(),
//                company.getType().name(),
//                company.getCompanyName(),
//                company.getEmail(),
//                Instant.now(),
//                buildMetadata(company.getId().value())
//        );
//        eventPublisher.publish("company.created", event);
//    }
//
//    private void publishCompanyProfileUpdatedEvent(Company company) {
//        CompanyProfileUpdatedEvent event = new CompanyProfileUpdatedEvent(
//                company.getId().value(),
//                company.getCompanyName(),
//                company.getCompanyEmail(),
//                Instant.now(),
//                buildMetadata(company.getId().value())
//        );
//        eventPublisher.publish("company.profile.updated", event);
//    }
//
//    private void publishCompanyAdminInvitedEvent(Invitation invitation) {
//        CompanyAdminInvitedEvent event = new CompanyAdminInvitedEvent(
//                invitation.getCompanyId().value(),
//                invitation.getEmail().value(),
//                invitation.getRole().name(),
//                invitation.getToken(),
//                Instant.now(),
//                buildMetadata(invitation.getCompanyId().value())
//        );
//        eventPublisher.publish("company.admin.invited", event);
//    }
//
//    private void publishCompanyMemberAddedEvent(CompanyMember member) {
//        CompanyMemberAddedEvent event = new CompanyMemberAddedEvent(
//                member.getCompanyId().value(),
//                member.getUserId(),
//                member.getRole().name(),
//                Instant.now(),
//                buildMetadata(member.getCompanyId().value())
//        );
//        eventPublisher.publish("company.member.added", event);
//    }
//
//    private EventMetadata buildMetadata(UUID aggregateId) {
//        return new EventMetadata(
//                UUID.randomUUID(),
//                "1.0",
//                "company-service",
//                securityContext.getTenantId(),
//                securityContext.getUserId()
//        );
//    }
//}
//OrganizationService.java
package com.svrmslk.company.organization.application;

import com.svrmslk.company.organization.application.command.CreateCompanyCommand;
import com.svrmslk.company.organization.application.command.InviteMemberCommand;
import com.svrmslk.company.organization.application.command.UpdateCompanyCommand;
import com.svrmslk.company.organization.domain.Company;
import com.svrmslk.company.organization.domain.CompanyMember;
import com.svrmslk.company.organization.domain.Invitation;
import com.svrmslk.company.organization.infrastructure.event.*;
import com.svrmslk.company.organization.infrastructure.persistence.*;
import com.svrmslk.company.shared.domain.*;
import com.svrmslk.company.shared.event.EventMetadata;
import com.svrmslk.company.shared.event.EventPublisher;
import com.svrmslk.company.shared.exception.CompanyNotFoundException;
import com.svrmslk.company.shared.exception.UnauthorizedException;
import com.svrmslk.company.shared.exception.ValidationException;
import com.svrmslk.company.shared.security.CompanySecurityContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrganizationService {

    private final CompanyRepository companyRepository;
    private final CompanyMemberRepository companyMemberRepository;
    private final InvitationRepository invitationRepository;
    private final CompanySecurityContext securityContext;
    private final EventPublisher eventPublisher;

    @Transactional
    public Company createCompany(CreateCompanyCommand command) {

        // ✅ Get userId — required
        UUID userId = securityContext.getUserId();

        // ✅ Get tenantId — may be null for new users during initial onboarding
        UUID tenantId = securityContext.getTenantId();
        if (tenantId == null) {
            // Fallback: use userId as tenantId for MVP onboarding
            // This gets updated once tenant-service assigns a real tenantId
            tenantId = userId;
            log.warn("TenantId is null for user: {}. Using userId as fallback tenantId during onboarding.", userId);
        }

        Company company = Company.builder()
                .id(CompanyId.generate())
                .tenantId(tenantId)                    // ✅ uses safe tenantId (userId fallback)
                .type(command.getType())
                .status(CompanyStatus.ACTIVE)
                .companyName(command.getCompanyName())
                .businessType(command.getBusinessType())
                .taxId(command.getTaxId())
                .registrationNumber(command.getRegistrationNumber())
                .companyEmail(command.getCompanyEmail())
                .companyPhone(command.getCompanyPhone())
                .firstName(command.getFirstName())
                .lastName(command.getLastName())
                .email(command.getEmail())
                .phone(command.getPhone())
                .streetAddress(command.getStreetAddress())
                .city(command.getCity())
                .zipCode(command.getZipCode())
                .organizationSize(command.getOrganizationSize())
                .paymentMethod(command.getPaymentMethod())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        company.validateIndividualLimits();

        Company savedCompany = companyRepository.save(company);

        // Add creator as COMPANY_ADMIN
        CompanyMember admin = CompanyMember.builder()
                .id(UUID.randomUUID())
                .companyId(savedCompany.getId())
                .userId(userId)
                .role(MemberRole.COMPANY_ADMIN)
                .joinedAt(Instant.now())
                .tenantId(tenantId)                    // ✅ uses safe tenantId
                .build();

        companyMemberRepository.save(admin);

        // Publish event
        publishCompanyCreatedEvent(savedCompany);

        log.info("Company created: {} for user: {} with tenantId: {}", savedCompany.getId(), userId, tenantId);
        return savedCompany;
    }

    @Transactional
    public Company updateCompany(UUID companyId, UpdateCompanyCommand command) {
        Company company = companyRepository.findById(new CompanyId(companyId))
                .orElseThrow(() -> new CompanyNotFoundException(companyId));

        verifyAccess(company);

        if (command.getCompanyName() != null) company.setCompanyName(command.getCompanyName());
        if (command.getBusinessType() != null) company.setBusinessType(command.getBusinessType());
        if (command.getCompanyEmail() != null) company.setCompanyEmail(command.getCompanyEmail());
        if (command.getCompanyPhone() != null) company.setCompanyPhone(command.getCompanyPhone());
        if (command.getFirstName() != null) company.setFirstName(command.getFirstName());
        if (command.getLastName() != null) company.setLastName(command.getLastName());
        if (command.getEmail() != null) company.setEmail(command.getEmail());
        if (command.getPhone() != null) company.setPhone(command.getPhone());
        if (command.getStreetAddress() != null) company.setStreetAddress(command.getStreetAddress());
        if (command.getCity() != null) company.setCity(command.getCity());
        if (command.getZipCode() != null) company.setZipCode(command.getZipCode());
        if (command.getPaymentMethod() != null) company.setPaymentMethod(command.getPaymentMethod());

        company.setUpdatedAt(Instant.now());

        Company updated = companyRepository.save(company);

        publishCompanyProfileUpdatedEvent(updated);

        return updated;
    }

    public Company getCompany(UUID companyId) {
        Company company = companyRepository.findById(new CompanyId(companyId))
                .orElseThrow(() -> new CompanyNotFoundException(companyId));

        verifyAccess(company);
        return company;
    }

    public List<Company> getCompaniesByUser() {
        UUID userId = securityContext.getUserId();
        return companyRepository.findByUserId(userId);
    }

    @Transactional
    public Invitation inviteMember(InviteMemberCommand command) {
        Company company = companyRepository.findById(new CompanyId(command.getCompanyId()))
                .orElseThrow(() -> new CompanyNotFoundException(command.getCompanyId()));

        verifyAdminAccess(company);

        // ✅ Safe tenantId retrieval
        UUID tenantId = securityContext.getTenantId();
        if (tenantId == null) {
            tenantId = securityContext.getUserId();
        }

        Invitation invitation = Invitation.builder()
                .id(UUID.randomUUID())
                .companyId(company.getId())
                .email(new Email(command.getEmail()))
                .role(command.getRole())
                .token(UUID.randomUUID().toString())
                .expiresAt(Instant.now().plus(7, ChronoUnit.DAYS))
                .accepted(false)
                .createdAt(Instant.now())
                .tenantId(tenantId)                    // ✅ uses safe tenantId
                .build();

        Invitation saved = invitationRepository.save(invitation);

        publishCompanyAdminInvitedEvent(saved);

        return saved;
    }

    @Transactional
    public CompanyMember acceptInvitation(String token, UUID userId) {
        Invitation invitation = invitationRepository.findByToken(token)
                .orElseThrow(() -> new ValidationException("Invalid invitation token"));

        if (invitation.isAccepted()) {
            throw new ValidationException("Invitation already accepted");
        }

        if (invitation.isExpired()) {
            throw new ValidationException("Invitation expired");
        }

        CompanyMember member = CompanyMember.builder()
                .id(UUID.randomUUID())
                .companyId(invitation.getCompanyId())
                .userId(userId)
                .role(invitation.getRole())
                .joinedAt(Instant.now())
                .tenantId(invitation.getTenantId())
                .build();

        companyMemberRepository.save(member);

        invitation.setAccepted(true);
        invitationRepository.save(invitation);

        publishCompanyMemberAddedEvent(member);

        return member;
    }

    public List<CompanyMember> getCompanyMembers(UUID companyId) {
        Company company = companyRepository.findById(new CompanyId(companyId))
                .orElseThrow(() -> new CompanyNotFoundException(companyId));

        verifyAccess(company);

        return companyMemberRepository.findByCompanyId(new CompanyId(companyId));
    }

    private void verifyAccess(Company company) {
        UUID userId = securityContext.getUserId();
        boolean isMember = companyMemberRepository.findByCompanyId(company.getId())
                .stream()
                .anyMatch(m -> m.getUserId().equals(userId));

        if (!isMember) {
            throw new UnauthorizedException("User is not a member of this company");
        }
    }

    private void verifyAdminAccess(Company company) {
        UUID userId = securityContext.getUserId();
        boolean isAdmin = companyMemberRepository.findByCompanyId(company.getId())
                .stream()
                .anyMatch(m -> m.getUserId().equals(userId) && m.getRole() == MemberRole.COMPANY_ADMIN);

        if (!isAdmin) {
            throw new UnauthorizedException("User is not an admin of this company");
        }
    }

    private void publishCompanyCreatedEvent(Company company) {
        CompanyCreatedEvent event = new CompanyCreatedEvent(
                company.getId().value(),
                company.getTenantId(),
                company.getType().name(),
                company.getCompanyName(),
                company.getEmail(),
                Instant.now(),
                buildMetadata(company.getId().value())
        );
        eventPublisher.publish("company.created", event);
    }

    private void publishCompanyProfileUpdatedEvent(Company company) {
        CompanyProfileUpdatedEvent event = new CompanyProfileUpdatedEvent(
                company.getId().value(),
                company.getCompanyName(),
                company.getCompanyEmail(),
                Instant.now(),
                buildMetadata(company.getId().value())
        );
        eventPublisher.publish("company.profile.updated", event);
    }

    private void publishCompanyAdminInvitedEvent(Invitation invitation) {
        CompanyAdminInvitedEvent event = new CompanyAdminInvitedEvent(
                invitation.getCompanyId().value(),
                invitation.getEmail().value(),
                invitation.getRole().name(),
                invitation.getToken(),
                Instant.now(),
                buildMetadata(invitation.getCompanyId().value())
        );
        eventPublisher.publish("company.admin.invited", event);
    }

    private void publishCompanyMemberAddedEvent(CompanyMember member) {
        CompanyMemberAddedEvent event = new CompanyMemberAddedEvent(
                member.getCompanyId().value(),
                member.getUserId(),
                member.getRole().name(),
                Instant.now(),
                buildMetadata(member.getCompanyId().value())
        );
        eventPublisher.publish("company.member.added", event);
    }

    /**
     * ✅ Build event metadata with safe tenantId handling
     */
    private EventMetadata buildMetadata(UUID aggregateId) {
        UUID tenantId = securityContext.getTenantId();
        UUID userId = securityContext.getUserId();

        // EventMetadata accepts null tenantId — it's optional in events
        return new EventMetadata(
                UUID.randomUUID(),
                "1.0",
                "company-service",
                tenantId,    // can be null during onboarding
                userId
        );
    }
}