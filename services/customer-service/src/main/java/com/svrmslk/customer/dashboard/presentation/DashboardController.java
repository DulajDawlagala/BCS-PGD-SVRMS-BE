// FILE: dashboard/presentation/DashboardController.java
package com.svrmslk.customer.dashboard.presentation;

import com.svrmslk.customer.dashboard.application.DashboardService;
import com.svrmslk.customer.dashboard.domain.CustomerDashboard;
import com.svrmslk.customer.dashboard.presentation.dto.DashboardResponse;
import com.svrmslk.customer.shared.api.ApiResponse;
import com.svrmslk.customer.shared.security.CustomerSecurityContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Customer Dashboard", description = "Customer dashboard and analytics APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class DashboardController {

    private final DashboardService dashboardService;
    private final CustomerSecurityContext securityContext;

    @GetMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Get current customer dashboard")
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard() {

        String customerId = securityContext.getAuthUserId();
        log.info("Fetching dashboard for customer: {}", customerId);

        CustomerDashboard dashboard = dashboardService.getDashboard(customerId);

        DashboardResponse response = new DashboardResponse(
                dashboard.getCustomerId(),
                dashboard.getTotalBookings(),
                dashboard.getActiveBookings(),
                dashboard.getCompletedBookings(),
                dashboard.getCancelledBookings()
        );

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
