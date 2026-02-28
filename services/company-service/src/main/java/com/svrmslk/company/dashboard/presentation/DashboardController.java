package com.svrmslk.company.dashboard.presentation;

import com.svrmslk.company.dashboard.application.DashboardService;
import com.svrmslk.company.dashboard.domain.CompanyDashboard;
import com.svrmslk.company.dashboard.domain.VehiclePerformance;
import com.svrmslk.company.dashboard.presentation.dto.DashboardResponse;
import com.svrmslk.company.dashboard.presentation.dto.VehiclePerformanceResponse;
import com.svrmslk.company.dashboard.presentation.mapper.DashboardDtoMapper;
import com.svrmslk.company.shared.api.ApiResponse;
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
@Tag(name = "Dashboard")
public class DashboardController {

    private final DashboardService dashboardService;
    private final DashboardDtoMapper mapper;

    @GetMapping("/company/{companyId}")
    @Operation(summary = "Get company dashboard")
    public ResponseEntity<ApiResponse<DashboardResponse>> getCompanyDashboard(@PathVariable UUID companyId) {
        CompanyDashboard dashboard = dashboardService.getCompanyDashboard(companyId);
        return ResponseEntity.ok(ApiResponse.success(mapper.toResponse(dashboard)));
    }

    @GetMapping("/company/{companyId}/vehicles/performance")
    @Operation(summary = "Get vehicle performance metrics")
    public ResponseEntity<ApiResponse<List<VehiclePerformanceResponse>>> getVehiclePerformance(
            @PathVariable UUID companyId) {

        List<VehiclePerformance> performances = dashboardService.getVehiclePerformance(companyId);
        List<VehiclePerformanceResponse> responses = performances.stream()
                .map(mapper::toPerformanceResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }
}