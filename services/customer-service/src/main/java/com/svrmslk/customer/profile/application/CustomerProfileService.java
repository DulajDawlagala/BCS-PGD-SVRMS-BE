//// FILE: profile/application/CustomerProfileService.java
//package com.svrmslk.customer.profile.application;
//
//import com.svrmslk.customer.profile.application.command.CreateCustomerCommand;
//import com.svrmslk.customer.profile.application.command.UpdateCustomerCommand;
//import com.svrmslk.customer.profile.domain.Customer;
//import com.svrmslk.customer.profile.domain.CustomerProfile;
//import com.svrmslk.customer.profile.infrastructure.event.CustomerCreatedEvent;
//import com.svrmslk.customer.profile.infrastructure.event.CustomerProfileUpdatedEvent;
//import com.svrmslk.customer.profile.infrastructure.persistence.CustomerRepository;
//import com.svrmslk.customer.profile.infrastructure.persistence.CustomerProfileRepository;
//import com.svrmslk.customer.shared.domain.CustomerId;
//import com.svrmslk.customer.shared.domain.Email;
//import com.svrmslk.customer.shared.event.EventPublisher;
//import com.svrmslk.customer.shared.exception.CustomerAlreadyExistsException;
//import com.svrmslk.customer.shared.exception.CustomerNotFoundException;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class CustomerProfileService {
//
//    private final CustomerRepository customerRepository;
//    private final CustomerProfileRepository profileRepository;
//    private final EventPublisher eventPublisher;
//
//    @Transactional
//    public CustomerId createCustomer(CreateCustomerCommand command) {
//        Email email = Email.of(command.email());
//
//        // Check for duplicates by email or authUserId
//        if (customerRepository.existsByEmail(email)) {
//            throw new CustomerAlreadyExistsException(command.email());
//        }
//        if (customerRepository.existsByAuthUserId(command.authUserId())) {
//            throw new CustomerAlreadyExistsException("User already has a customer profile");
//        }
//
//        // Create customer
//        Customer customer = Customer.create(command.authUserId(), email);
//        customer = customerRepository.save(customer);
//
//        // Create profile
//        CustomerProfile profile = CustomerProfile.createDefault(customer.getId());
//        profile.updateBasicInfo(command.firstName(), command.lastName(), command.phoneNumber());
//        profileRepository.save(profile);
//
//        // Publish event
//        eventPublisher.publish(new CustomerCreatedEvent(
//                customer.getId().getValue(),
//                email.getValue(),
//                command.firstName(),
//                command.lastName()
//        ));
//
//        return customer.getId();
//    }
//
//
//    @Transactional(readOnly = true)
//    public Customer getCustomer(CustomerId customerId) {
//        return customerRepository.findById(customerId)
//                .orElseThrow(() -> new CustomerNotFoundException(customerId.getValue()));
//    }
//
//    @Transactional(readOnly = true)
//    public CustomerProfile getProfile(CustomerId customerId) {
//        return profileRepository.findByCustomerId(customerId)
//                .orElseThrow(() -> new CustomerNotFoundException(customerId.getValue()));
//    }
//
//    @Transactional
//    public void updateProfile(UpdateCustomerCommand command) {
//        log.info("Updating profile for customer: {}", command.customerId());
//
//        CustomerId customerId = CustomerId.of(command.customerId());
//
//        // Verify customer exists
//        Customer customer = getCustomer(customerId);
//
//        // Update profile
//        CustomerProfile profile = getProfile(customerId);
//        profile.updateBasicInfo(
//                command.firstName(),
//                command.lastName(),
//                command.phoneNumber()
//        );
//
//        if (command.driversLicenseNumber() != null) {
//            profile.updateDriverInfo(
//                    command.driversLicenseNumber(),
//                    command.licenseExpiryDate()
//            );
//        }
//
//        profileRepository.save(profile);
//
//        // Publish event
//        eventPublisher.publish(new CustomerProfileUpdatedEvent(
//                customerId.getValue(),
//                command.firstName(),
//                command.lastName()
//        ));
//
//        log.info("Profile updated for customer: {}", command.customerId());
//    }
//
//    @Transactional
//    public void recordLogin(CustomerId customerId) {
//        Customer customer = getCustomer(customerId);
//        customer.recordLogin();
//        customerRepository.save(customer);
//    }
//}

// FILE: profile/application/CustomerProfileService.java
package com.svrmslk.customer.profile.application;

import com.svrmslk.customer.profile.application.command.CreateCustomerCommand;
import com.svrmslk.customer.profile.application.command.UpdateCustomerCommand;
import com.svrmslk.customer.profile.domain.Customer;
import com.svrmslk.customer.profile.domain.CustomerProfile;
import com.svrmslk.customer.profile.infrastructure.event.CustomerCreatedEvent;
import com.svrmslk.customer.profile.infrastructure.event.CustomerProfileUpdatedEvent;
import com.svrmslk.customer.profile.infrastructure.persistence.CustomerRepository;
import com.svrmslk.customer.profile.infrastructure.persistence.CustomerProfileRepository;
import com.svrmslk.customer.shared.domain.CustomerId;
import com.svrmslk.customer.shared.domain.Email;
import com.svrmslk.customer.shared.event.EventPublisher;
import com.svrmslk.customer.shared.exception.CustomerAlreadyExistsException;
import com.svrmslk.customer.shared.exception.CustomerNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CustomerProfileService {

    private final CustomerRepository customerRepository;
    private final CustomerProfileRepository profileRepository;
    private final EventPublisher eventPublisher;

    // -------------------------------------------------
    // CREATE CUSTOMER
    // -------------------------------------------------
    public CustomerId createCustomer(CreateCustomerCommand command) {

        Email email = Email.of(command.email());

        if (customerRepository.existsByEmail(email)) {
            throw new CustomerAlreadyExistsException(command.email());
        }

        if (customerRepository.existsByAuthUserId(command.authUserId())) {
            throw new CustomerAlreadyExistsException(
                    "User already has a customer profile"
            );
        }

        Customer customer = Customer.create(
                command.authUserId(),
                email
        );

        customer = customerRepository.save(customer);

        CustomerProfile profile = CustomerProfile.createDefault(customer.getId());
        profile.updateBasicInfo(
                command.firstName(),
                command.lastName(),
                command.phoneNumber()
        );
        profileRepository.save(profile);

        eventPublisher.publish(new CustomerCreatedEvent(
                customer.getId().getValue(),
                email.getValue(),
                command.firstName(),
                command.lastName()
        ));

        return customer.getId();
    }

    // -------------------------------------------------
    // READ OPERATIONS
    // -------------------------------------------------
    @Transactional(readOnly = true)
    public Customer getCustomer(CustomerId customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() ->
                        new CustomerNotFoundException(customerId.getValue())
                );
    }

    @Transactional(readOnly = true)
    public CustomerProfile getProfile(CustomerId customerId) {
        return profileRepository.findByCustomerId(customerId)
                .orElseThrow(() ->
                        new CustomerNotFoundException(customerId.getValue())
                );
    }

    /**
     * 🔥 REQUIRED BY CONTROLLERS & SECURITY FLOW
     */
    @Transactional(readOnly = true)
    public Customer getByAuthUserId(String authUserId) {
        return customerRepository.findByAuthUserId(authUserId)
                .orElseThrow(() ->
                        new CustomerNotFoundException(
                                "Customer profile not found for authenticated user"
                        )
                );
    }

    // -------------------------------------------------
    // UPDATE PROFILE
    // -------------------------------------------------
    public void updateProfile(UpdateCustomerCommand command) {

        CustomerId customerId = CustomerId.of(command.customerId());

        // Ensure customer exists
        getCustomer(customerId);

        CustomerProfile profile = getProfile(customerId);
        profile.updateBasicInfo(
                command.firstName(),
                command.lastName(),
                command.phoneNumber()
        );

        if (command.driversLicenseNumber() != null) {
            profile.updateDriverInfo(
                    command.driversLicenseNumber(),
                    command.licenseExpiryDate()
            );
        }

        profileRepository.save(profile);

        eventPublisher.publish(new CustomerProfileUpdatedEvent(
                customerId.getValue(),
                command.firstName(),
                command.lastName()
        ));
    }

    // -------------------------------------------------
    // LOGIN TRACKING
    // -------------------------------------------------
    public void recordLogin(CustomerId customerId) {
        Customer customer = getCustomer(customerId);
        customer.recordLogin();
        customerRepository.save(customer);
    }
}
