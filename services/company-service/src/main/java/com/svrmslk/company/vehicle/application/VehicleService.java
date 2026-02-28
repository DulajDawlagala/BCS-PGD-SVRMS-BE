////VehicleService.java
//package com.svrmslk.company.vehicle.application;
//
//import com.svrmslk.company.organization.domain.Company;
//import com.svrmslk.company.organization.infrastructure.persistence.CompanyRepository;
//import com.svrmslk.company.shared.domain.CompanyId;
//import com.svrmslk.company.shared.domain.CompanyType;
//import com.svrmslk.company.shared.domain.VehicleId;
//import com.svrmslk.company.shared.event.EventMetadata;
//import com.svrmslk.company.shared.event.EventPublisher;
//import com.svrmslk.company.shared.exception.CompanyNotFoundException;
//import com.svrmslk.company.shared.exception.UnauthorizedException;
//import com.svrmslk.company.shared.exception.ValidationException;
//import com.svrmslk.company.shared.exception.VehicleNotFoundException;
//import com.svrmslk.company.shared.security.CompanySecurityContext;
//import com.svrmslk.company.vehicle.application.command.AddVehicleCommand;
//import com.svrmslk.company.vehicle.application.command.UpdateVehicleCommand;
//import com.svrmslk.company.vehicle.domain.Vehicle;
//import com.svrmslk.company.vehicle.domain.VehicleStatus;
//import com.svrmslk.company.vehicle.infrastructure.event.VehicleAddedEvent;
//import com.svrmslk.company.vehicle.infrastructure.event.VehicleStatusChangedEvent;
//import com.svrmslk.company.vehicle.infrastructure.event.VehicleUpdatedEvent;
//import com.svrmslk.company.vehicle.infrastructure.persistence.VehicleRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.Instant;
//import java.util.List;
//import java.util.UUID;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class VehicleService {
//
//    private final VehicleRepository vehicleRepository;
//    private final CompanyRepository companyRepository;
//    private final CompanySecurityContext securityContext;
//    private final EventPublisher eventPublisher;
//
//    @Transactional
//    public Vehicle addVehicle(AddVehicleCommand command) {
//        Company company = companyRepository.findById(new CompanyId(command.getCompanyId()))
//                .orElseThrow(() -> new CompanyNotFoundException(command.getCompanyId()));
//
//        verifyCompanyAccess(company);
//
//        // Check individual vehicle limits
//        if (company.getType() == CompanyType.INDIVIDUAL) {
//            long vehicleCount = vehicleRepository.countByCompanyId(company.getId());
//            if (vehicleCount >= company.getMaxVehicles()) {
//                throw new ValidationException("Individual owner vehicle limit reached: " + company.getMaxVehicles());
//            }
//        }
//
//        Vehicle vehicle = Vehicle.builder()
//                .id(VehicleId.generate())
//                .companyId(company.getId())
//                .tenantId(securityContext.getTenantId())
//                .make(command.getMake())
//                .model(command.getModel())
//                .year(command.getYear())
//                .color(command.getColor())
//                .licensePlate(command.getLicensePlate())
//                .vin(command.getVin())
//                .imageUrls(command.getImageUrls())
//                .registrationDocUrl(command.getRegistrationDocUrl())
//                .insuranceDocUrl(command.getInsuranceDocUrl())
//                .hourlyRate(command.getHourlyRate())
//                .dailyRate(command.getDailyRate())
//                .weeklyRate(command.getWeeklyRate())
//                .monthlyRate(command.getMonthlyRate())
//                .availableDays(command.getAvailableDays())
//                .minRentalHours(command.getMinRentalHours())
//                .maxRentalDays(command.getMaxRentalDays())
//                .status(VehicleStatus.AVAILABLE)
//                .createdAt(Instant.now())
//                .updatedAt(Instant.now())
//                .build();
//
//        Vehicle saved = vehicleRepository.save(vehicle);
//
//        publishVehicleAddedEvent(saved);
//
//        log.info("Vehicle added: {}", saved.getId());
//        return saved;
//    }
//
//    @Transactional
//    public Vehicle updateVehicle(UUID vehicleId, UpdateVehicleCommand command) {
//        Vehicle vehicle = vehicleRepository.findById(new VehicleId(vehicleId))
//                .orElseThrow(() -> new VehicleNotFoundException(vehicleId));
//
//        Company company = companyRepository.findById(vehicle.getCompanyId())
//                .orElseThrow(() -> new CompanyNotFoundException(vehicle.getCompanyId().value()));
//
//        verifyCompanyAccess(company);
//
//        VehicleStatus oldStatus = vehicle.getStatus();
//
//        if (command.getColor() != null) vehicle.setColor(command.getColor());
//        if (command.getImageUrls() != null) vehicle.setImageUrls(command.getImageUrls());
//        if (command.getRegistrationDocUrl() != null) vehicle.setRegistrationDocUrl(command.getRegistrationDocUrl());
//        if (command.getInsuranceDocUrl() != null) vehicle.setInsuranceDocUrl(command.getInsuranceDocUrl());
//        if (command.getHourlyRate() != null) vehicle.setHourlyRate(command.getHourlyRate());
//        if (command.getDailyRate() != null) vehicle.setDailyRate(command.getDailyRate());
//        if (command.getWeeklyRate() != null) vehicle.setWeeklyRate(command.getWeeklyRate());
//        if (command.getMonthlyRate() != null) vehicle.setMonthlyRate(command.getMonthlyRate());
//        if (command.getAvailableDays() != null) vehicle.setAvailableDays(command.getAvailableDays());
//        if (command.getMinRentalHours() != null) vehicle.setMinRentalHours(command.getMinRentalHours());
//        if (command.getMaxRentalDays() != null) vehicle.setMaxRentalDays(command.getMaxRentalDays());
//        if (command.getStatus() != null) vehicle.setStatus(command.getStatus());
//
//        vehicle.setUpdatedAt(Instant.now());
//
//        Vehicle updated = vehicleRepository.save(vehicle);
//
//        publishVehicleUpdatedEvent(updated);
//
//        if (oldStatus != updated.getStatus()) {
//            publishVehicleStatusChangedEvent(updated, oldStatus);
//        }
//
//        return updated;
//    }
//
//    public Vehicle getVehicle(UUID vehicleId) {
//        Vehicle vehicle = vehicleRepository.findById(new VehicleId(vehicleId))
//                .orElseThrow(() -> new VehicleNotFoundException(vehicleId));
//
//        Company company = companyRepository.findById(vehicle.getCompanyId())
//                .orElseThrow(() -> new CompanyNotFoundException(vehicle.getCompanyId().value()));
//
//        verifyCompanyAccess(company);
//
//        return vehicle;
//    }
//
//    public List<Vehicle> getVehiclesByCompany(UUID companyId) {
//        Company company = companyRepository.findById(new CompanyId(companyId))
//                .orElseThrow(() -> new CompanyNotFoundException(companyId));
//
//        verifyCompanyAccess(company);
//
//        return vehicleRepository.findByCompanyId(company.getId());
//    }
//
//    @Transactional
//    public void deleteVehicle(UUID vehicleId) {
//        Vehicle vehicle = vehicleRepository.findById(new VehicleId(vehicleId))
//                .orElseThrow(() -> new VehicleNotFoundException(vehicleId));
//
//        Company company = companyRepository.findById(vehicle.getCompanyId())
//                .orElseThrow(() -> new CompanyNotFoundException(vehicle.getCompanyId().value()));
//
//        verifyCompanyAccess(company);
//
//        vehicleRepository.delete(vehicle.getId());
//        log.info("Vehicle deleted: {}", vehicleId);
//    }
//
//    private void verifyCompanyAccess(Company company) {
//        UUID userId = securityContext.getUserId();
//        // In real implementation, check if user is member of company
//        // For now, simplified
//    }
//
//    private void publishVehicleAddedEvent(Vehicle vehicle) {
//        VehicleAddedEvent event = new VehicleAddedEvent(
//                vehicle.getId().value(),
//                vehicle.getCompanyId().value(),
//                vehicle.getMake(),
//                vehicle.getModel(),
//                vehicle.getYear(),
//                Instant.now(),
//                buildMetadata(vehicle.getId().value())
//        );
//        eventPublisher.publish("vehicle.added", event);
//    }
//
//    private void publishVehicleUpdatedEvent(Vehicle vehicle) {
//        VehicleUpdatedEvent event = new VehicleUpdatedEvent(
//                vehicle.getId().value(),
//                vehicle.getCompanyId().value(),
//                vehicle.getStatus().name(),
//                Instant.now(),
//                buildMetadata(vehicle.getId().value())
//        );
//        eventPublisher.publish("vehicle.updated", event);
//    }
//
//    private void publishVehicleStatusChangedEvent(Vehicle vehicle, VehicleStatus oldStatus) {
//        VehicleStatusChangedEvent event = new VehicleStatusChangedEvent(
//                vehicle.getId().value(),
//                vehicle.getCompanyId().value(),
//                oldStatus.name(),
//                vehicle.getStatus().name(),
//                Instant.now(),
//                buildMetadata(vehicle.getId().value())
//        );
//        eventPublisher.publish("vehicle.status.changed", event);
//    }
//
//    private EventMetadata buildMetadata(UUID aggregateId) {
//        return new EventMetadata(
//                UUID.randomUUID(),
//                "1.0",
//                "company-service",
//                securityContext.getTenantId(),
//                securityContext.getUserId()
//        );
//    }
//}

package com.svrmslk.company.vehicle.application;

import com.svrmslk.company.organization.domain.Company;
import com.svrmslk.company.organization.infrastructure.persistence.CompanyRepository;
import com.svrmslk.company.shared.domain.CompanyId;
import com.svrmslk.company.shared.domain.CompanyType;
import com.svrmslk.company.shared.domain.VehicleId;
import com.svrmslk.company.shared.event.EventMetadata;
import com.svrmslk.company.shared.event.EventPublisher;
import com.svrmslk.company.shared.exception.CompanyNotFoundException;
import com.svrmslk.company.shared.exception.ValidationException;
import com.svrmslk.company.shared.exception.VehicleNotFoundException;
import com.svrmslk.company.shared.security.CompanySecurityContext;
import com.svrmslk.company.vehicle.application.command.AddVehicleCommand;
import com.svrmslk.company.vehicle.application.command.UpdateVehicleCommand;
import com.svrmslk.company.vehicle.domain.Vehicle;
import com.svrmslk.company.vehicle.domain.VehicleStatus;
import com.svrmslk.company.vehicle.infrastructure.event.VehicleAddedEvent;
import com.svrmslk.company.vehicle.infrastructure.event.VehicleStatusChangedEvent;
import com.svrmslk.company.vehicle.infrastructure.event.VehicleUpdatedEvent;
import com.svrmslk.company.vehicle.infrastructure.persistence.VehicleJpaRepository;
import com.svrmslk.company.vehicle.infrastructure.persistence.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleJpaRepository vehicleJpaRepository;
    private final CompanyRepository companyRepository;
    private final CompanySecurityContext securityContext;
    private final EventPublisher eventPublisher;

    @Transactional
    public Vehicle addVehicle(AddVehicleCommand command) {
        Company company = companyRepository.findById(new CompanyId(command.getCompanyId()))
                .orElseThrow(() -> new CompanyNotFoundException(command.getCompanyId()));

        verifyCompanyAccess(company);

        // Check individual vehicle limits
        if (company.getType() == CompanyType.INDIVIDUAL) {
            long vehicleCount = vehicleRepository.countByCompanyId(company.getId());
            if (vehicleCount >= company.getMaxVehicles()) {
                throw new ValidationException("Individual owner vehicle limit reached: " + company.getMaxVehicles());
            }
        }

        // ✅ Safe tenantId handling
        UUID tenantId = securityContext.getTenantId();
        if (tenantId == null) {
            tenantId = securityContext.getUserId();
        }

        Vehicle vehicle = Vehicle.builder()
                .id(VehicleId.generate())
                .companyId(company.getId())
                .tenantId(tenantId)
                .make(command.getMake())
                .model(command.getModel())
                .year(command.getYear())
                .color(command.getColor())
                .licensePlate(command.getLicensePlate())
                .vin(command.getVin())
                .imageUrls(command.getImageUrls())
                .registrationDocUrl(command.getRegistrationDocUrl())
                .insuranceDocUrl(command.getInsuranceDocUrl())
                .hourlyRate(command.getHourlyRate())
                .dailyRate(command.getDailyRate())
                .weeklyRate(command.getWeeklyRate())
                .monthlyRate(command.getMonthlyRate())
                .availableDays(command.getAvailableDays())
                .minRentalHours(command.getMinRentalHours())
                .maxRentalDays(command.getMaxRentalDays())
                .status(VehicleStatus.AVAILABLE)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        Vehicle saved = vehicleRepository.save(vehicle);

        publishVehicleAddedEvent(saved);

        log.info("Vehicle added: {}", saved.getId());
        return saved;
    }

    @Transactional
    public Vehicle updateVehicle(UUID vehicleId, UpdateVehicleCommand command) {
        Vehicle vehicle = vehicleRepository.findById(new VehicleId(vehicleId))
                .orElseThrow(() -> new VehicleNotFoundException(vehicleId));

        Company company = companyRepository.findById(vehicle.getCompanyId())
                .orElseThrow(() -> new CompanyNotFoundException(vehicle.getCompanyId().value()));

        verifyCompanyAccess(company);

        VehicleStatus oldStatus = vehicle.getStatus();

        if (command.getColor() != null) vehicle.setColor(command.getColor());
        if (command.getImageUrls() != null) vehicle.setImageUrls(command.getImageUrls());
        if (command.getRegistrationDocUrl() != null) vehicle.setRegistrationDocUrl(command.getRegistrationDocUrl());
        if (command.getInsuranceDocUrl() != null) vehicle.setInsuranceDocUrl(command.getInsuranceDocUrl());
        if (command.getHourlyRate() != null) vehicle.setHourlyRate(command.getHourlyRate());
        if (command.getDailyRate() != null) vehicle.setDailyRate(command.getDailyRate());
        if (command.getWeeklyRate() != null) vehicle.setWeeklyRate(command.getWeeklyRate());
        if (command.getMonthlyRate() != null) vehicle.setMonthlyRate(command.getMonthlyRate());
        if (command.getAvailableDays() != null) vehicle.setAvailableDays(command.getAvailableDays());
        if (command.getMinRentalHours() != null) vehicle.setMinRentalHours(command.getMinRentalHours());
        if (command.getMaxRentalDays() != null) vehicle.setMaxRentalDays(command.getMaxRentalDays());
        if (command.getStatus() != null) vehicle.setStatus(command.getStatus());

        vehicle.setUpdatedAt(Instant.now());

        Vehicle updated = vehicleRepository.save(vehicle);

        publishVehicleUpdatedEvent(updated);

        if (oldStatus != updated.getStatus()) {
            publishVehicleStatusChangedEvent(updated, oldStatus);
        }

        return updated;
    }

    // ✅ Get all AVAILABLE vehicles (public - no auth required in service logic)
    public List<Vehicle> getAvailableVehicles(String make, String model, Integer minYear, Integer maxYear) {
        // Use repository method instead of direct JPA access
        List<Vehicle> allVehicles = vehicleRepository.findByStatus(VehicleStatus.AVAILABLE);

        // Apply filters
        return allVehicles.stream()
                .filter(v -> make == null || v.getMake().equalsIgnoreCase(make))
                .filter(v -> model == null || v.getModel().toLowerCase().contains(model.toLowerCase()))
                .filter(v -> minYear == null || v.getYear() >= minYear)
                .filter(v -> maxYear == null || v.getYear() <= maxYear)
                .collect(Collectors.toList());
    }

    // ✅ NEW: Update vehicle status (MAINTENANCE, AVAILABLE)
    @Transactional
    public Vehicle updateVehicleStatus(UUID vehicleId, VehicleStatus newStatus) {
        Vehicle vehicle = vehicleRepository.findById(new VehicleId(vehicleId))
                .orElseThrow(() -> new VehicleNotFoundException(vehicleId));

        Company company = companyRepository.findById(vehicle.getCompanyId())
                .orElseThrow(() -> new CompanyNotFoundException(vehicle.getCompanyId().value()));

        verifyCompanyAccess(company);

        VehicleStatus oldStatus = vehicle.getStatus();

        // Prevent invalid status transitions
//        if (newStatus == VehicleStatus.RENTED) {
//            throw new ValidationException("Cannot manually set vehicle to RENTED status");
//        }

        vehicle.setStatus(newStatus);
        vehicle.setUpdatedAt(Instant.now());

        Vehicle updated = vehicleRepository.save(vehicle);

        publishVehicleStatusChangedEvent(updated, oldStatus);

        log.info("Vehicle status changed: {} from {} to {}", vehicleId, oldStatus, newStatus);
        return updated;
    }

    public Vehicle getVehicle(UUID vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(new VehicleId(vehicleId))
                .orElseThrow(() -> new VehicleNotFoundException(vehicleId));

        Company company = companyRepository.findById(vehicle.getCompanyId())
                .orElseThrow(() -> new CompanyNotFoundException(vehicle.getCompanyId().value()));

        verifyCompanyAccess(company);

        return vehicle;
    }

    public List<Vehicle> getVehiclesByCompany(UUID companyId) {
        Company company = companyRepository.findById(new CompanyId(companyId))
                .orElseThrow(() -> new CompanyNotFoundException(companyId));

        verifyCompanyAccess(company);

        return vehicleRepository.findByCompanyId(company.getId());
    }

    @Transactional
    public void deleteVehicle(UUID vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(new VehicleId(vehicleId))
                .orElseThrow(() -> new VehicleNotFoundException(vehicleId));

        Company company = companyRepository.findById(vehicle.getCompanyId())
                .orElseThrow(() -> new CompanyNotFoundException(vehicle.getCompanyId().value()));

        verifyCompanyAccess(company);

        vehicleRepository.delete(vehicle.getId());
        log.info("Vehicle deleted: {}", vehicleId);
    }

    private void verifyCompanyAccess(Company company) {
        UUID userId = securityContext.getUserId();
        // Simplified for MVP - in production, check company_members table
    }

    private void publishVehicleAddedEvent(Vehicle vehicle) {
        VehicleAddedEvent event = new VehicleAddedEvent(
                vehicle.getId().value(),
                vehicle.getCompanyId().value(),
                vehicle.getMake(),
                vehicle.getModel(),
                vehicle.getYear(),
                Instant.now(),
                buildMetadata(vehicle.getId().value())
        );
        eventPublisher.publish("vehicle.added", event);
    }

    private void publishVehicleUpdatedEvent(Vehicle vehicle) {
        VehicleUpdatedEvent event = new VehicleUpdatedEvent(
                vehicle.getId().value(),
                vehicle.getCompanyId().value(),
                vehicle.getStatus().name(),
                Instant.now(),
                buildMetadata(vehicle.getId().value())
        );
        eventPublisher.publish("vehicle.updated", event);
    }

    private void publishVehicleStatusChangedEvent(Vehicle vehicle, VehicleStatus oldStatus) {
        VehicleStatusChangedEvent event = new VehicleStatusChangedEvent(
                vehicle.getId().value(),
                vehicle.getCompanyId().value(),
                oldStatus.name(),
                vehicle.getStatus().name(),
                Instant.now(),
                buildMetadata(vehicle.getId().value())
        );
        eventPublisher.publish("vehicle.status.changed", event);
    }

    private EventMetadata buildMetadata(UUID aggregateId) {
        UUID tenantId = securityContext.getTenantId();
        if (tenantId == null) {
            tenantId = securityContext.getUserId();
        }
        return new EventMetadata(
                UUID.randomUUID(),
                "1.0",
                "company-service",
                tenantId,
                securityContext.getUserId()
        );
    }
}