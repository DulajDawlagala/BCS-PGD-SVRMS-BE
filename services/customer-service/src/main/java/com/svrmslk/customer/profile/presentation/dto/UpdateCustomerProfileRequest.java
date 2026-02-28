// FILE: profile/presentation/dto/UpdateCustomerProfileRequest.java
package com.svrmslk.customer.profile.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record UpdateCustomerProfileRequest(

        @NotBlank(message = "First name is required")
        @Size(min = 1, max = 100)
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(min = 1, max = 100)
        String lastName,

        @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number")
        String phoneNumber,

        @Past(message = "Date of birth must be in the past")
        LocalDate dateOfBirth,

        @Size(max = 50)
        String nationality,

        @Size(max = 50)
        String driversLicenseNumber,

        LocalDate licenseExpiryDate
) {}