package com.svrmslk.company.vehicle.infrastructure.persistence;

import com.svrmslk.company.shared.domain.CompanyId;
import com.svrmslk.company.shared.domain.VehicleId;
import com.svrmslk.company.vehicle.domain.Vehicle;
import org.springframework.stereotype.Component;

@Component
public class VehicleEntityMapper {

    public VehicleEntity toEntity(Vehicle vehicle) {
        return VehicleEntity.builder()
                .id(vehicle.getId().value())
                .companyId(vehicle.getCompanyId().value())
                .tenantId(vehicle.getTenantId())
                .make(vehicle.getMake())
                .model(vehicle.getModel())
                .year(vehicle.getYear())
                .color(vehicle.getColor())
                .licensePlate(vehicle.getLicensePlate())
                .vin(vehicle.getVin())
                .imageUrls(vehicle.getImageUrls())
                .registrationDocUrl(vehicle.getRegistrationDocUrl())
                .insuranceDocUrl(vehicle.getInsuranceDocUrl())
                .hourlyRate(vehicle.getHourlyRate())
                .dailyRate(vehicle.getDailyRate())
                .weeklyRate(vehicle.getWeeklyRate())
                .monthlyRate(vehicle.getMonthlyRate())
                .availableDays(vehicle.getAvailableDays())
                .minRentalHours(vehicle.getMinRentalHours())
                .maxRentalDays(vehicle.getMaxRentalDays())
                .status(vehicle.getStatus())
                .createdAt(vehicle.getCreatedAt())
                .updatedAt(vehicle.getUpdatedAt())
                .build();
    }

    public Vehicle toDomain(VehicleEntity entity) {
        return Vehicle.builder()
                .id(new VehicleId(entity.getId()))
                .companyId(new CompanyId(entity.getCompanyId()))
                .tenantId(entity.getTenantId())
                .make(entity.getMake())
                .model(entity.getModel())
                .year(entity.getYear())
                .color(entity.getColor())
                .licensePlate(entity.getLicensePlate())
                .vin(entity.getVin())
                .imageUrls(entity.getImageUrls())
                .registrationDocUrl(entity.getRegistrationDocUrl())
                .insuranceDocUrl(entity.getInsuranceDocUrl())
                .hourlyRate(entity.getHourlyRate())
                .dailyRate(entity.getDailyRate())
                .weeklyRate(entity.getWeeklyRate())
                .monthlyRate(entity.getMonthlyRate())
                .availableDays(entity.getAvailableDays())
                .minRentalHours(entity.getMinRentalHours())
                .maxRentalDays(entity.getMaxRentalDays())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}