package com.svrmslk.company.organization.application.command;

import com.svrmslk.company.shared.domain.MemberRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InviteMemberCommand {
    private UUID companyId;
    private String email;
    private MemberRole role;
}