//// FILE: profile/domain/CustomerProfile.java
//package com.svrmslk.customer.profile.domain;
//
//import com.svrmslk.customer.shared.domain.CustomerId;
//import java.time.LocalDate;
//
//public class CustomerProfile {
//
//    private CustomerId customerId;
//    private String firstName;
//    private String lastName;
//    private String phoneNumber;
//    private LocalDate dateOfBirth;
//    private String nationality;
//    private String driversLicenseNumber;
//    private LocalDate licenseExpiryDate;
//    private Preferences preferences;
//
//    public static CustomerProfile createDefault(CustomerId customerId) {
//        CustomerProfile profile = new CustomerProfile();
//        profile.customerId = customerId;
//        profile.preferences = Preferences.createDefault();
//        return profile;
//    }
//
//    public void updateBasicInfo(String firstName, String lastName, String phoneNumber) {
//        this.firstName = firstName;
//        this.lastName = lastName;
//        this.phoneNumber = phoneNumber;
//    }
//
//    public void updateDriverInfo(String driversLicenseNumber, LocalDate licenseExpiryDate) {
//        this.driversLicenseNumber = driversLicenseNumber;
//        this.licenseExpiryDate = licenseExpiryDate;
//    }
//
//    public boolean hasValidDriversLicense() {
//        if (driversLicenseNumber == null || licenseExpiryDate == null) {
//            return false;
//        }
//        return licenseExpiryDate.isAfter(LocalDate.now());
//    }
//
//    public String getFullName() {
//        return firstName + " " + lastName;
//    }
//
//    // Getters
//    public CustomerId getCustomerId() { return customerId; }
//    public String getFirstName() { return firstName; }
//    public String getLastName() { return lastName; }
//    public String getPhoneNumber() { return phoneNumber; }
//    public LocalDate getDateOfBirth() { return dateOfBirth; }
//    public String getNationality() { return nationality; }
//    public String getDriversLicenseNumber() { return driversLicenseNumber; }
//    public LocalDate getLicenseExpiryDate() { return licenseExpiryDate; }
//    public Preferences getPreferences() { return preferences; }
//
//    // Package-private setters
//    void setCustomerId(CustomerId customerId) { this.customerId = customerId; }
//    void setFirstName(String firstName) { this.firstName = firstName; }
//    void setLastName(String lastName) { this.lastName = lastName; }
//    void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
//    void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
//    void setNationality(String nationality) { this.nationality = nationality; }
//    void setDriversLicenseNumber(String driversLicenseNumber) {
//        this.driversLicenseNumber = driversLicenseNumber;
//    }
//    void setLicenseExpiryDate(LocalDate licenseExpiryDate) {
//        this.licenseExpiryDate = licenseExpiryDate;
//    }
//    void setPreferences(Preferences preferences) { this.preferences = preferences; }
//}

// FILE: profile/domain/CustomerProfile.java
package com.svrmslk.customer.profile.domain;

import com.svrmslk.customer.shared.domain.CustomerId;

import java.time.LocalDate;
import java.util.Objects;

public class CustomerProfile {

    private final CustomerId customerId;

    private String firstName;
    private String lastName;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String nationality;
    private String driversLicenseNumber;
    private LocalDate licenseExpiryDate;
    private Preferences preferences;

    /**
     * Private canonical constructor.
     */
    private CustomerProfile(
            CustomerId customerId,
            String firstName,
            String lastName,
            String phoneNumber,
            LocalDate dateOfBirth,
            String nationality,
            String driversLicenseNumber,
            LocalDate licenseExpiryDate,
            Preferences preferences
    ) {
        this.customerId = Objects.requireNonNull(customerId, "customerId");
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.nationality = nationality;
        this.driversLicenseNumber = driversLicenseNumber;
        this.licenseExpiryDate = licenseExpiryDate;
        this.preferences = Objects.requireNonNull(preferences, "preferences");
    }

    /* =========================
       FACTORIES
       ========================= */

    /**
     * Creation factory for a NEW customer profile.
     */
    public static CustomerProfile createDefault(CustomerId customerId) {
        return new CustomerProfile(
                customerId,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                Preferences.createDefault()
        );
    }

    /**
     * Rehydration factory for persistence.
     * Used ONLY by infrastructure mappers.
     */
    public static CustomerProfile rehydrate(
            CustomerId customerId,
            String firstName,
            String lastName,
            String phoneNumber,
            LocalDate dateOfBirth,
            String nationality,
            String driversLicenseNumber,
            LocalDate licenseExpiryDate,
            Preferences preferences
    ) {
        return new CustomerProfile(
                customerId,
                firstName,
                lastName,
                phoneNumber,
                dateOfBirth,
                nationality,
                driversLicenseNumber,
                licenseExpiryDate,
                preferences
        );
    }

    /* =========================
       DOMAIN BEHAVIOR
       ========================= */

    public void updateBasicInfo(String firstName, String lastName, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
    }

    public void updateDriverInfo(String driversLicenseNumber, LocalDate licenseExpiryDate) {
        this.driversLicenseNumber = driversLicenseNumber;
        this.licenseExpiryDate = licenseExpiryDate;
    }

    public boolean hasValidDriversLicense() {
        return driversLicenseNumber != null
                && licenseExpiryDate != null
                && licenseExpiryDate.isAfter(LocalDate.now());
    }

    public String getFullName() {
        if (firstName == null && lastName == null) {
            return "";
        }
        if (firstName == null) {
            return lastName;
        }
        if (lastName == null) {
            return firstName;
        }
        return firstName + " " + lastName;
    }

    /* =========================
       GETTERS (READ-ONLY)
       ========================= */

    public CustomerId getCustomerId() {
        return customerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getNationality() {
        return nationality;
    }

    public String getDriversLicenseNumber() {
        return driversLicenseNumber;
    }

    public LocalDate getLicenseExpiryDate() {
        return licenseExpiryDate;
    }

    public Preferences getPreferences() {
        return preferences;
    }
}
