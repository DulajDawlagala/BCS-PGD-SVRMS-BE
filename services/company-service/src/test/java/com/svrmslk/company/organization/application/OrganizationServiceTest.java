package com.svrmslk.company.organization.application;

import com.svrmslk.company.organization.application.command.CreateCompanyCommand;
import com.svrmslk.company.organization.domain.Company;
import com.svrmslk.company.organization.infrastructure.persistence.CompanyMemberRepository;
import com.svrmslk.company.organization.infrastructure.persistence.CompanyRepository;
import com.svrmslk.company.organization.infrastructure.persistence.InvitationRepository;
import com.svrmslk.company.shared.domain.CompanyType;
import com.svrmslk.company.shared.event.EventPublisher;
import com.svrmslk.company.shared.security.CompanySecurityContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrganizationServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private CompanyMemberRepository companyMemberRepository;

    @Mock
    private InvitationRepository invitationRepository;

    @Mock
    private CompanySecurityContext securityContext;

    @Mock
    private EventPublisher eventPublisher;

    private OrganizationService organizationService;

    @BeforeEach
    void setUp() {
        organizationService = new OrganizationService(
                companyRepository,
                companyMemberRepository,
                invitationRepository,
                securityContext,
                eventPublisher
        );

        when(securityContext.getTenantId()).thenReturn(UUID.randomUUID());
        when(securityContext.getUserId()).thenReturn(UUID.randomUUID());
    }

    @Test
    void createCompany_shouldCreateIndividualOwner() {
        CreateCompanyCommand command = CreateCompanyCommand.builder()
                .type(CompanyType.INDIVIDUAL)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .build();

        when(companyRepository.save(any(Company.class))).thenAnswer(i -> i.getArguments()[0]);

        Company result = organizationService.createCompany(command);

        assertNotNull(result);
        assertEquals(CompanyType.INDIVIDUAL, result.getType());
        assertEquals(5, result.getMaxVehicles());
        verify(companyRepository).save(any(Company.class));
        verify(companyMemberRepository).save(any());
    }
}