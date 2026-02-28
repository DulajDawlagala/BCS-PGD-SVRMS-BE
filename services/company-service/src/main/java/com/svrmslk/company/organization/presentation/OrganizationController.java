//OrganizationController.java
package com.svrmslk.company.organization.presentation;

import com.svrmslk.company.organization.application.OrganizationService;
import com.svrmslk.company.organization.application.command.CreateCompanyCommand;
import com.svrmslk.company.organization.application.command.InviteMemberCommand;
import com.svrmslk.company.organization.application.command.UpdateCompanyCommand;
import com.svrmslk.company.organization.domain.Company;
import com.svrmslk.company.organization.domain.CompanyMember;
import com.svrmslk.company.organization.domain.Invitation;
import com.svrmslk.company.organization.presentation.dto.*;
import com.svrmslk.company.organization.presentation.mapper.OrganizationDtoMapper;
import com.svrmslk.company.shared.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/organizations")
@RequiredArgsConstructor
@Tag(name = "Organization Management")
public class OrganizationController {

    private final OrganizationService organizationService;
    private final OrganizationDtoMapper mapper;

    @PostMapping
    @Operation(summary = "Create company or register as individual owner")
    public ResponseEntity<ApiResponse<CompanyResponse>> createCompany(
            @RequestBody CreateCompanyRequest request) {

        CreateCompanyCommand command = mapper.toCreateCommand(request);
        Company company = organizationService.createCompany(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(mapper.toResponse(company), "Company created successfully"));
    }

    @PutMapping("/{companyId}")
    @Operation(summary = "Update company profile")
    public ResponseEntity<ApiResponse<CompanyResponse>> updateCompany(
            @PathVariable UUID companyId,
            @RequestBody UpdateCompanyRequest request) {

        UpdateCompanyCommand command = mapper.toUpdateCommand(request);
        Company company = organizationService.updateCompany(companyId, command);

        return ResponseEntity.ok(ApiResponse.success(mapper.toResponse(company)));
    }

    @GetMapping("/{companyId}")
    @Operation(summary = "Get company details")
    public ResponseEntity<ApiResponse<CompanyResponse>> getCompany(@PathVariable UUID companyId) {
        Company company = organizationService.getCompany(companyId);
        return ResponseEntity.ok(ApiResponse.success(mapper.toResponse(company)));
    }

    @GetMapping
    @Operation(summary = "Get all companies for current user")
    public ResponseEntity<ApiResponse<List<CompanyResponse>>> getMyCompanies() {
        List<Company> companies = organizationService.getCompaniesByUser();
        List<CompanyResponse> responses = companies.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @PostMapping("/{companyId}/invitations")
    @Operation(summary = "Invite member to company")
    public ResponseEntity<ApiResponse<InvitationResponse>> inviteMember(
            @PathVariable UUID companyId,
            @RequestBody InviteMemberRequest request) {

        InviteMemberCommand command = InviteMemberCommand.builder()
                .companyId(companyId)
                .email(request.getEmail())
                .role(request.getRole())
                .build();

        Invitation invitation = organizationService.inviteMember(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(mapper.toInvitationResponse(invitation)));
    }

    @PostMapping("/invitations/{token}/accept")
    @Operation(summary = "Accept invitation")
    public ResponseEntity<ApiResponse<CompanyMemberResponse>> acceptInvitation(
            @PathVariable String token,
            @RequestBody AcceptInvitationRequest request) {

        CompanyMember member = organizationService.acceptInvitation(token, request.getUserId());

        return ResponseEntity.ok(ApiResponse.success(mapper.toMemberResponse(member)));
    }

    @GetMapping("/{companyId}/members")
    @Operation(summary = "Get company members")
    public ResponseEntity<ApiResponse<List<CompanyMemberResponse>>> getCompanyMembers(
            @PathVariable UUID companyId) {

        List<CompanyMember> members = organizationService.getCompanyMembers(companyId);
        List<CompanyMemberResponse> responses = members.stream()
                .map(mapper::toMemberResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }
}