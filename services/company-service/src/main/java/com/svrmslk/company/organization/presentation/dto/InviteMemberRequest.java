package com.svrmslk.company.organization.presentation.dto;

import com.svrmslk.company.shared.domain.MemberRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InviteMemberRequest {
    private String email;
    private MemberRole role;
}