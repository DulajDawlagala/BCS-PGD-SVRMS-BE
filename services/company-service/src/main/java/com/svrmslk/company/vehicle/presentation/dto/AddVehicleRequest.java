package com.svrmslk.company.vehicle.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddVehicleRequest {
    private UUID companyId;
    private String make;
    private String model;
    private Integer year;
    private String color;
    private String licensePlate;
    private String vin;
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
}