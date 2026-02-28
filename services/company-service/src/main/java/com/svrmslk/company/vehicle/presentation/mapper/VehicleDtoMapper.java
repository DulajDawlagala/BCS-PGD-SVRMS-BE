package com.svrmslk.company.vehicle.presentation.mapper;

import com.svrmslk.company.vehicle.application.command.AddVehicleCommand;
import com.svrmslk.company.vehicle.application.command.UpdateVehicleCommand;
import com.svrmslk.company.vehicle.domain.Vehicle;
import com.svrmslk.company.vehicle.presentation.dto.AddVehicleRequest;
import com.svrmslk.company.vehicle.presentation.dto.UpdateVehicleRequest;
import com.svrmslk.company.vehicle.presentation.dto.VehicleResponse;
import org.springframework.stereotype.Component;

@Component
public class VehicleDtoMapper {

    public AddVehicleCommand toAddCommand(AddVehicleRequest request) {
        return AddVehicleCommand.builder()
                .companyId(request.getCompanyId())
                .make(request.getMake())
                .model(request.getModel())
                .year(request.getYear())
                .color(request.getColor())
                .licensePlate(request.getLicensePlate())
                .vin(request.getVin())
                .imageUrls(request.getImageUrls())
                .registrationDocUrl(request.getRegistrationDocUrl())
                .insuranceDocUrl(request.getInsuranceDocUrl())
                .hourlyRate(request.getHourlyRate())
                .dailyRate(request.getDailyRate())
                .weeklyRate(request.getWeeklyRate())
                .monthlyRate(request.getMonthlyRate())
                .availableDays(request.getAvailableDays())
                .minRentalHours(request.getMinRentalHours())
                .maxRentalDays(request.getMaxRentalDays())
                .build();
    }

    public UpdateVehicleCommand toUpdateCommand(UpdateVehicleRequest request) {
        return UpdateVehicleCommand.builder()
                .color(request.getColor())
                .imageUrls(request.getImageUrls())
                .registrationDocUrl(request.getRegistrationDocUrl())
                .insuranceDocUrl(request.getInsuranceDocUrl())
                .hourlyRate(request.getHourlyRate())
                .dailyRate(request.getDailyRate())
                .weeklyRate(request.getWeeklyRate())
                .monthlyRate(request.getMonthlyRate())
                .availableDays(request.getAvailableDays())
                .minRentalHours(request.getMinRentalHours())
                .maxRentalDays(request.getMaxRentalDays())
                .status(request.getStatus())
                .build();
    }

    public VehicleResponse toResponse(Vehicle vehicle) {
        return VehicleResponse.builder()
                .id(vehicle.getId().value())
                .companyId(vehicle.getCompanyId().value())
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
}