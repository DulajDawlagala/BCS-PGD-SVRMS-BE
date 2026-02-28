package com.svrmslk.company.vehicle.presentation.dto;

import com.svrmslk.company.vehicle.domain.VehicleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateVehicleRequest {
    private String color;
    private List<String> imageUrls;
    private String registrationDocUrl;
    private String insuranceDocUrl;
    private BigDecimal hourlyRate;
    private BigDecimal dailyRate;
    private BigDecimal weeklyRate;
    private BigDecimal monthlyRate;
    private List<String> availableDays;
    private Integer minRentalHours;
    private Integer maxRentalDays;
    private VehicleStatus status;
}