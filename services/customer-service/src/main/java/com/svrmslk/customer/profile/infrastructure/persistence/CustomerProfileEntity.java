// FILE: profile/infrastructure/persistence/CustomerProfileEntity.java
package com.svrmslk.customer.profile.infrastructure.persistence;

import com.svrmslk.customer.profile.domain.Preferences;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "customer_profiles", indexes = {
        @Index(name = "idx_profile_customer_id", columnList = "customer_id", unique = true)
})
@EntityListeners(AuditingEntityListener.class)
public class CustomerProfileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", nullable = false, unique = true, length = 36)
    private String customerId;

    @Column(name = "first_name", length = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "nationality", length = 50)
    private String nationality;

    @Column(name = "drivers_license_number", length = 50)
    private String driversLicenseNumber;

    @Column(name = "license_expiry_date")
    private LocalDate licenseExpiryDate;

    @Embedded
    private Preferences preferences;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }

    public String getDriversLicenseNumber() { return driversLicenseNumber; }
    public void setDriversLicenseNumber(String driversLicenseNumber) {
        this.driversLicenseNumber = driversLicenseNumber;
    }

    public LocalDate getLicenseExpiryDate() { return licenseExpiryDate; }
    public void setLicenseExpiryDate(LocalDate licenseExpiryDate) {
        this.licenseExpiryDate = licenseExpiryDate;
    }

    public Preferences getPreferences() { return preferences; }
    public void setPreferences(Preferences preferences) { this.preferences = preferences; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
