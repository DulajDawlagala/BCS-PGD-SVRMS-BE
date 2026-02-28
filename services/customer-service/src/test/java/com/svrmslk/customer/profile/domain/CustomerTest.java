//// FILE: profile/domain/CustomerTest.java
//package com.svrmslk.customer.profile.domain;
//
//import com.svrmslk.customer.shared.domain.CustomerStatus;
//import com.svrmslk.customer.shared.domain.Email;
//import org.junit.jupiter.api.Test;
//import static org.assertj.core.api.Assertions.*;
//
//class CustomerTest {
//
//    @Test
//    void shouldCreateCustomerWithActiveStatus() {
//        // Given
//        Email email = Email.of("test@example.com");
//
//        // When
//        Customer customer = Customer.create(email);
//
//        // Then
//        assertThat(customer.getEmail()).isEqualTo(email);
//        assertThat(customer.getStatus()).isEqualTo(CustomerStatus.ACTIVE);
//        assertThat(customer.canBook()).isTrue();
//    }
//
//    @Test
//    void shouldSuspendActiveCustomer() {
//        // Given
//        Customer customer = Customer.create(Email.of("test@example.com"));
//
//        // When
//        customer.suspend();
//
//        // Then
//        assertThat(customer.getStatus()).isEqualTo(CustomerStatus.SUSPENDED);
//        assertThat(customer.canBook()).isFalse();
//    }
//
//    @Test
//    void shouldNotSuspendAlreadySuspendedCustomer() {
//        // Given
//        Customer customer = Customer.create(Email.of("test@example.com"));
//        customer.suspend();
//
//        // When/Then
//        assertThatThrownBy(() -> customer.suspend())
//                .isInstanceOf(IllegalStateException.class)
//                .hasMessageContaining("already suspended");
//    }
//
//    @Test
//    void shouldActivateSuspendedCustomer() {
//        // Given
//        Customer customer = Customer.create(Email.of("test@example.com"));
//        customer.suspend();
//
//        // When
//        customer.activate();
//
//        // Then
//        assertThat(customer.getStatus()).isEqualTo(CustomerStatus.ACTIVE);
//        assertThat(customer.canBook()).isTrue();
//    }
//}

package com.svrmslk.customer.profile.domain;

import com.svrmslk.customer.shared.domain.CustomerStatus;
import com.svrmslk.customer.shared.domain.Email;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class CustomerTest {

    private static final String AUTH_USER_ID = "auth-user-test-1";

    @Test
    void shouldCreateCustomerWithActiveStatus() {
        // Given
        Email email = Email.of("test@example.com");

        // When
        Customer customer = Customer.create(AUTH_USER_ID, email);

        // Then
        assertThat(customer.getEmail()).isEqualTo(email);
        assertThat(customer.getStatus()).isEqualTo(CustomerStatus.ACTIVE);
        assertThat(customer.canBook()).isTrue();
    }

    @Test
    void shouldSuspendActiveCustomer() {
        // Given
        Customer customer = Customer.create(
                AUTH_USER_ID,
                Email.of("test@example.com")
        );

        // When
        customer.suspend();

        // Then
        assertThat(customer.getStatus()).isEqualTo(CustomerStatus.SUSPENDED);
        assertThat(customer.canBook()).isFalse();
    }

    @Test
    void shouldNotSuspendAlreadySuspendedCustomer() {
        // Given
        Customer customer = Customer.create(
                AUTH_USER_ID,
                Email.of("test@example.com")
        );
        customer.suspend();

        // When / Then
        assertThatThrownBy(customer::suspend)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already suspended");
    }

    @Test
    void shouldActivateSuspendedCustomer() {
        // Given
        Customer customer = Customer.create(
                AUTH_USER_ID,
                Email.of("test@example.com")
        );
        customer.suspend();

        // When
        customer.activate();

        // Then
        assertThat(customer.getStatus()).isEqualTo(CustomerStatus.ACTIVE);
        assertThat(customer.canBook()).isTrue();
    }
}
