package com.svrmslk.company.vehicle.domain;

import com.svrmslk.company.shared.domain.CompanyId;
import com.svrmslk.company.shared.domain.VehicleId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {
    private VehicleId id;
    private CompanyId companyId;
    private UUID tenantId;

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

    private VehicleStatus status;

    private Instant createdAt;
    private Instant updatedAt;
}