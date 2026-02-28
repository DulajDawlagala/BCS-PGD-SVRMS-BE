//// FILE: profile/domain/Customer.java
//package com.svrmslk.customer.profile.domain;
//
//import com.svrmslk.customer.shared.domain.CustomerId;
//import com.svrmslk.customer.shared.domain.CustomerStatus;
//import com.svrmslk.customer.shared.domain.Email;
//
//import java.time.LocalDateTime;
//import java.util.Objects;
//
//public class Customer {
//
//    private final CustomerId id;
//    private final Email email;
//    private CustomerStatus status;
//    private final LocalDateTime registeredAt;
//    private LocalDateTime lastLoginAt;
//    private final String authUserId;
//
//    /**
//     * Private canonical constructor.
//     * All invariants are enforced here.
//     */
//    private Customer(
//            CustomerId id,
//            Email email,
//            CustomerStatus status,
//            LocalDateTime registeredAt,
//            LocalDateTime lastLoginAt,
//            String authUserId,
//    ) {
//        this.id = Objects.requireNonNull(id, "id");
//        this.email = Objects.requireNonNull(email, "email");
//        this.status = Objects.requireNonNull(status, "status");
//        this.registeredAt = Objects.requireNonNull(registeredAt, "registeredAt");
//        this.lastLoginAt = lastLoginAt;
//        this.authUserId = authUserId;
//    }
//
//    /* =========================
//       FACTORIES
//       ========================= */
//
//    /**
//     * Creation factory for NEW customers.
//     */
////    public static Customer create(Email email) {
////        return new Customer(
////                CustomerId.generate(),
////                email,
////                CustomerStatus.ACTIVE,
////                LocalDateTime.now(),
////                null
////        );
////    }
//
//    public static Customer create(String authUserId, Email email) {
//        Customer c = new Customer();
//        c.setCustomerId(UUID.randomUUID().toString());
//        c.setAuthUserId(authUserId);
//        c.setEmail(email);
//        c.setStatus(CustomerStatus.ACTIVE);
//        c.setRegisteredAt(LocalDateTime.now());
//        return c;
//    }
//
//    /**
//     * Rehydration factory for persistence.
//     * Used ONLY by infrastructure mappers.
//     */
//    public static Customer rehydrate(
//            CustomerId id,
//            Email email,
//            CustomerStatus status,
//            LocalDateTime registeredAt,
//            LocalDateTime lastLoginAt
//    ) {
//        return new Customer(
//                id,
//                email,
//                status,
//                registeredAt,
//                lastLoginAt
//        );
//    }
//
//    /* =========================
//       BEHAVIOR (DOMAIN LOGIC)
//       ========================= */
//
//    public void suspend() {
//        if (this.status == CustomerStatus.SUSPENDED) {
//            throw new IllegalStateException("Customer already suspended");
//        }
//        this.status = CustomerStatus.SUSPENDED;
//    }
//
//    public void activate() {
//        if (this.status == CustomerStatus.ACTIVE) {
//            throw new IllegalStateException("Customer already active");
//        }
//        this.status = CustomerStatus.ACTIVE;
//    }
//
//    public void recordLogin() {
//        this.lastLoginAt = LocalDateTime.now();
//    }
//
//    public boolean canBook() {
//        return status.canBook();
//    }
//
//    /* =========================
//       GETTERS (READ-ONLY)
//       ========================= */
//
//    public CustomerId getId() {
//        return id;
//    }
//
//    public Email getEmail() {
//        return email;
//    }
//
//    public CustomerStatus getStatus() {
//        return status;
//    }
//
//    public LocalDateTime getRegisteredAt() {
//        return registeredAt;
//    }
//
//    public LocalDateTime getLastLoginAt() {
//        return lastLoginAt;
//    }
//}

// FILE: profile/domain/Customer.java
package com.svrmslk.customer.profile.domain;

import com.svrmslk.customer.shared.domain.CustomerId;
import com.svrmslk.customer.shared.domain.CustomerStatus;
import com.svrmslk.customer.shared.domain.Email;

import java.time.LocalDateTime;
import java.util.Objects;

public class Customer {

    private final CustomerId id;
    private final Email email;
    private CustomerStatus status;
    private final LocalDateTime registeredAt;
    private LocalDateTime lastLoginAt;
    private final String authUserId;

    /**
     * Private canonical constructor. Ensures all invariants.
     */
    private Customer(
            CustomerId id,
            Email email,
            CustomerStatus status,
            LocalDateTime registeredAt,
            LocalDateTime lastLoginAt,
            String authUserId
    ) {
        this.id = Objects.requireNonNull(id, "CustomerId is required");
        this.email = Objects.requireNonNull(email, "Email is required");
        this.status = Objects.requireNonNull(status, "Status is required");
        this.registeredAt = Objects.requireNonNull(registeredAt, "RegisteredAt is required");
        this.lastLoginAt = lastLoginAt;
        this.authUserId = Objects.requireNonNull(authUserId, "AuthUserId is required");
    }

    /* =========================
       FACTORIES
       ========================= */

    /**
     * Factory for creating new customers.
     * Ensures authUserId and email are not null.
     */
    public static Customer create(String authUserId, Email email) {
        Objects.requireNonNull(authUserId, "AuthUserId is required");
        Objects.requireNonNull(email, "Email is required");

        return new Customer(
                CustomerId.generate(),
                email,
                CustomerStatus.ACTIVE,
                LocalDateTime.now(),
                null,
                authUserId
        );
    }

    /**
     * Factory for rehydrating from persistence layer.
     */
    public static Customer rehydrate(
            CustomerId id,
            Email email,
            String authUserId,
            CustomerStatus status,
            LocalDateTime registeredAt,
            LocalDateTime lastLoginAt
    ) {
        return new Customer(
                id,
                email,
                status,
                registeredAt,
                lastLoginAt,
                authUserId
        );
    }

    /* =========================
       DOMAIN LOGIC
       ========================= */

    public void suspend() {
        if (this.status == CustomerStatus.SUSPENDED) {
            throw new IllegalStateException("Customer already suspended");
        }
        this.status = CustomerStatus.SUSPENDED;
    }

    public void activate() {
        if (this.status == CustomerStatus.ACTIVE) {
            throw new IllegalStateException("Customer already active");
        }
        this.status = CustomerStatus.ACTIVE;
    }

    public void recordLogin() {
        this.lastLoginAt = LocalDateTime.now();
    }

    public boolean canBook() {
        return status.canBook();
    }

    /* =========================
       GETTERS
       ========================= */

    public CustomerId getId() { return id; }

    public Email getEmail() { return email; }

    public CustomerStatus getStatus() { return status; }

    public LocalDateTime getRegisteredAt() { return registeredAt; }

    public LocalDateTime getLastLoginAt() { return lastLoginAt; }

    public String getAuthUserId() { return authUserId; }
}


