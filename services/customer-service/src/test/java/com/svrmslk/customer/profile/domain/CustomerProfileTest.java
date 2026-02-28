// FILE: profile/domain/CustomerProfileTest.java
package com.svrmslk.customer.profile.domain;

import com.svrmslk.customer.shared.domain.CustomerId;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.*;

class CustomerProfileTest {

    @Test
    void shouldCreateDefaultProfile() {
        // Given
        CustomerId customerId = CustomerId.generate();

        // When
        CustomerProfile profile = CustomerProfile.createDefault(customerId);

        // Then
        assertThat(profile.getCustomerId()).isEqualTo(customerId);
        assertThat(profile.getPreferences()).isNotNull();
        assertThat(profile.getPreferences().isEmailNotifications()).isTrue();
    }

    @Test
    void shouldUpdateBasicInfo() {
        // Given
        CustomerProfile profile = CustomerProfile.createDefault(CustomerId.generate());

        // When
        profile.updateBasicInfo("John", "Doe", "+1234567890");

        // Then
        assertThat(profile.getFirstName()).isEqualTo("John");
        assertThat(profile.getLastName()).isEqualTo("Doe");
        assertThat(profile.getFullName()).isEqualTo("John Doe");
    }

    @Test
    void shouldValidateDriversLicense() {
        // Given
        CustomerProfile profile = CustomerProfile.createDefault(CustomerId.generate());

        // When
        profile.updateDriverInfo("DL123456", LocalDate.now().plusYears(1));

        // Then
        assertThat(profile.hasValidDriversLicense()).isTrue();
    }

    @Test
    void shouldDetectExpiredLicense() {
        // Given
        CustomerProfile profile = CustomerProfile.createDefault(CustomerId.generate());

        // When
        profile.updateDriverInfo("DL123456", LocalDate.now().minusDays(1));

        // Then
        assertThat(profile.hasValidDriversLicense()).isFalse();
    }
}
