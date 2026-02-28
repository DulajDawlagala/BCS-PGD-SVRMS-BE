package com.svrmslk.company.shared.exception;

import java.util.UUID;

public class VehicleNotFoundException extends DomainException {
    public VehicleNotFoundException(UUID vehicleId) {
        super("Vehicle not found: " + vehicleId);
    }
}