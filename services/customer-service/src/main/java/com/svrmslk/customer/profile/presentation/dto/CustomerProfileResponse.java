// FILE: profile/presentation/dto/CustomerProfileResponse.java
package com.svrmslk.customer.profile.presentation.dto;

import com.svrmslk.customer.profile.domain.Preferences;
import com.svrmslk.customer.shared.domain.CustomerStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record CustomerProfileResponse(
        String customerId,
        String email,
        CustomerStatus status,
        String firstName,
        String lastName,
        String phoneNumber,
        LocalDate dateOfBirth,
        String nationality,
        String driversLicenseNumber,
        LocalDate licenseExpiryDate,
        Preferences preferences,
        LocalDateTime registeredAt,
        LocalDateTime lastLoginAt
) {}