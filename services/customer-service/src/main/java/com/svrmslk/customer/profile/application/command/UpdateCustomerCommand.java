// FILE: profile/application/command/UpdateCustomerCommand.java
package com.svrmslk.customer.profile.application.command;

import java.time.LocalDate;

public record UpdateCustomerCommand(
        String customerId,
        String firstName,
        String lastName,
        String phoneNumber,
        LocalDate dateOfBirth,
        String nationality,
        String driversLicenseNumber,
        LocalDate licenseExpiryDate
) {
    public UpdateCustomerCommand {
        if (customerId == null || customerId.isBlank()) {
            throw new IllegalArgumentException("Customer ID is required");
        }
    }
}