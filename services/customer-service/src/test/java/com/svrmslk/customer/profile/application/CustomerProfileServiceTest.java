//// FILE: profile/application/CustomerProfileServiceTest.java
//package com.svrmslk.customer.profile.application;
//
//import com.svrmslk.customer.profile.application.command.CreateCustomerCommand;
//import com.svrmslk.customer.profile.domain.Customer;
//import com.svrmslk.customer.profile.domain.CustomerProfile;
//import com.svrmslk.customer.profile.infrastructure.event.CustomerCreatedEvent;
//import com.svrmslk.customer.profile.infrastructure.persistence.CustomerProfileRepository;
//import com.svrmslk.customer.profile.infrastructure.persistence.CustomerRepository;
//import com.svrmslk.customer.shared.domain.CustomerId;
//import com.svrmslk.customer.shared.domain.Email;
//import com.svrmslk.customer.shared.event.EventPublisher;
//import com.svrmslk.customer.shared.exception.CustomerAlreadyExistsException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import static org.assertj.core.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class CustomerProfileServiceTest {
//
//    @Mock
//    private CustomerRepository customerRepository;
//
//    @Mock
//    private CustomerProfileRepository profileRepository;
//
//    @Mock
//    private EventPublisher eventPublisher;
//
//    @InjectMocks
//    private CustomerProfileService service;
//
//    @Test
//    void shouldCreateCustomerSuccessfully() {
//        // Given
//        CreateCustomerCommand command = new CreateCustomerCommand(
//                "test@example.com",
//                "John",
//                "Doe",
//                "+1234567890"
//        );
//
//        Customer.create(
//                "auth-user-test-1",
//                Email.of("test@example.com")
//        );
//
//
//        when(customerRepository.existsByEmail(any())).thenReturn(false);
//        when(customerRepository.save(any())).thenReturn(customer);
//        when(profileRepository.save(any())).thenReturn(CustomerProfile.createDefault(customer.getId()));
//
//        // When
//        CustomerId customerId = service.createCustomer(command);
//
//        // Then
//        assertThat(customerId).isNotNull();
//        verify(customerRepository).save(any());
//        verify(profileRepository).save(any());
//        verify(eventPublisher).publish(any(CustomerCreatedEvent.class));
//    }
//
//    @Test
//    void shouldThrowExceptionWhenCustomerAlreadyExists() {
//        // Given
//        CreateCustomerCommand command = new CreateCustomerCommand(
//                "test@example.com",
//                "John",
//                "Doe",
//                "+1234567890"
//        );
//
//        when(customerRepository.existsByEmail(any())).thenReturn(true);
//
//        // When/Then
//        assertThatThrownBy(() -> service.createCustomer(command))
//                .isInstanceOf(CustomerAlreadyExistsException.class);
//
//        verify(customerRepository, never()).save(any());
//    }
//}


package com.svrmslk.customer.profile.application;

import com.svrmslk.customer.profile.application.command.CreateCustomerCommand;
import com.svrmslk.customer.profile.domain.Customer;
import com.svrmslk.customer.profile.domain.CustomerProfile;
import com.svrmslk.customer.profile.infrastructure.event.CustomerCreatedEvent;
import com.svrmslk.customer.profile.infrastructure.persistence.CustomerProfileRepository;
import com.svrmslk.customer.profile.infrastructure.persistence.CustomerRepository;
import com.svrmslk.customer.shared.domain.CustomerId;
import com.svrmslk.customer.shared.domain.Email;
import com.svrmslk.customer.shared.event.EventPublisher;
import com.svrmslk.customer.shared.exception.CustomerAlreadyExistsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerProfileServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerProfileRepository profileRepository;

    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private CustomerProfileService service;

    private static final String AUTH_USER_ID = "auth-user-test-1";

    @Test
    void shouldCreateCustomerSuccessfully() {
        // Given
        CreateCustomerCommand command = new CreateCustomerCommand(
                AUTH_USER_ID,
                "test@example.com",
                "John",
                "Doe",
                "+1234567890"
        );

        Customer customer = Customer.create(
                AUTH_USER_ID,
                Email.of("test@example.com")
        );

        when(customerRepository.existsByEmail(any())).thenReturn(false);
        when(customerRepository.existsByAuthUserId(AUTH_USER_ID)).thenReturn(false);
        when(customerRepository.save(any())).thenReturn(customer);
        when(profileRepository.save(any()))
                .thenReturn(CustomerProfile.createDefault(customer.getId()));

        // When
        CustomerId customerId = service.createCustomer(command);

        // Then
        assertThat(customerId).isNotNull();
        verify(customerRepository).save(any());
        verify(profileRepository).save(any());
        verify(eventPublisher).publish(any(CustomerCreatedEvent.class));
    }

    @Test
    void shouldThrowExceptionWhenCustomerAlreadyExists() {
        // Given
        CreateCustomerCommand command = new CreateCustomerCommand(
                AUTH_USER_ID,
                "test@example.com",
                "John",
                "Doe",
                "+1234567890"
        );

        when(customerRepository.existsByEmail(any())).thenReturn(true);

        // When / Then
        assertThatThrownBy(() -> service.createCustomer(command))
                .isInstanceOf(CustomerAlreadyExistsException.class);

        verify(customerRepository, never()).save(any());
        verify(profileRepository, never()).save(any());
    }
}
