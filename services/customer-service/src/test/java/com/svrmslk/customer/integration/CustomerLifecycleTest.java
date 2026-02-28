// FILE: integration/CustomerLifecycleTest.java
package com.svrmslk.customer.integration;

import com.svrmslk.customer.profile.application.CustomerProfileService;
import com.svrmslk.customer.profile.application.command.CreateCustomerCommand;
import com.svrmslk.customer.profile.application.command.UpdateCustomerCommand;
import com.svrmslk.customer.profile.domain.Customer;
import com.svrmslk.customer.profile.domain.CustomerProfile;
import com.svrmslk.customer.shared.domain.CustomerId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CustomerLifecycleTest {

    @Autowired
    private CustomerProfileService profileService;

    @Test
    void shouldCreateAndUpdateCustomerProfile() {
        // Given: Create customer
        CreateCustomerCommand createCommand = new CreateCustomerCommand(
                "auth-user-integration-1",   // ✅ authUserId
                "integration@test.com",
                "Integration",
                "Test",
                "+1234567890"
        );


        // When: Create
        CustomerId customerId = profileService.createCustomer(createCommand);

        // Then: Verify creation
        assertThat(customerId).isNotNull();
        Customer customer = profileService.getCustomer(customerId);
        assertThat(customer.getEmail().getValue()).isEqualTo("integration@test.com");

        // When: Update profile
        UpdateCustomerCommand updateCommand = new UpdateCustomerCommand(
                customerId.getValue(),
                "Updated",
                "Name",
                "+9876543210",
                LocalDate.of(1990, 1, 1),
                "US",
                "DL123456",
                LocalDate.now().plusYears(5)
        );

        profileService.updateProfile(updateCommand);

        // Then: Verify update
        CustomerProfile profile = profileService.getProfile(customerId);
        assertThat(profile.getFirstName()).isEqualTo("Updated");
        assertThat(profile.getLastName()).isEqualTo("Name");
        assertThat(profile.hasValidDriversLicense()).isTrue();
    }
}
