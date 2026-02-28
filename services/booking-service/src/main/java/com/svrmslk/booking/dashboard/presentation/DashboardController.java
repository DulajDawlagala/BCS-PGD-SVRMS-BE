package com.svrmslk.booking.dashboard.presentation;

import com.svrmslk.booking.dashboard.application.DashboardService;
import com.svrmslk.booking.dashboard.domain.CompanyDashboard;
import com.svrmslk.booking.dashboard.domain.CustomerDashboard;
import com.svrmslk.booking.dashboard.domain.VehiclePerformance;
import com.svrmslk.booking.dashboard.presentation.dto.CompanyDashboardResponse;
import com.svrmslk.booking.dashboard.presentation.dto.CustomerDashboardResponse;
import com.svrmslk.booking.dashboard.presentation.dto.VehiclePerformanceResponse;
import com.svrmslk.booking.dashboard.presentation.mapper.DashboardDtoMapper;
import com.svrmslk.booking.shared.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard Analytics")
public class DashboardController {

    private final DashboardService dashboardService;
    private final DashboardDtoMapper mapper;

//    @GetMapping("/me")
//    @Operation(summary = "Get customer dashboard")
//    public ResponseEntity<ApiResponse<CustomerDashboardResponse>> getCustomerDashboard() {
//        CustomerDashboard dashboard = dashboardService.getCustomerDashboard();
//        return ResponseEntity.ok(ApiResponse.success(mapper.toCustomerResponse(dashboard)));
//    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<CustomerDashboardResponse>> getCustomerDashboard(
            @RequestHeader("X-USER-ID") String userId,
            @RequestHeader(value = "X-TENANT-ID", required = false) String tenantId) {

        // Convert to UUIDs, handling null for tenant if necessary
        UUID userUuid = UUID.fromString(userId);
        UUID tenantUuid = (tenantId != null && !tenantId.equals("null")) ? UUID.fromString(tenantId) : null;

        CustomerDashboard dashboard = dashboardService.getCustomerDashboard(userUuid, tenantUuid);
        return ResponseEntity.ok(ApiResponse.success(mapper.toCustomerResponse(dashboard)));
    }

    @GetMapping("/company/{companyId}")
    @Operation(summary = "Get company dashboard")
    public ResponseEntity<ApiResponse<CompanyDashboardResponse>> getCompanyDashboard(@PathVariable UUID companyId) {
        CompanyDashboard dashboard = dashboardService.getCompanyDashboard(companyId);
        return ResponseEntity.ok(ApiResponse.success(mapper.toCompanyResponse(dashboard)));
    }

    @GetMapping("/company/{companyId}/vehicles/performance")
    @Operation(summary = "Get vehicle performance metrics")
    public ResponseEntity<ApiResponse<List<VehiclePerformanceResponse>>> getVehiclePerformance(
            @PathVariable UUID companyId) {

        List<VehiclePerformance> performances = dashboardService.getVehiclePerformance(companyId);
        List<VehiclePerformanceResponse> responses = performances.stream()
                .map(mapper::toVehiclePerformanceResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }
}