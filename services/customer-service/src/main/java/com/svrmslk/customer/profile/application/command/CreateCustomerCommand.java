// FILE: profile/application/command/CreateCustomerCommand.java
package com.svrmslk.customer.profile.application.command;

//public record CreateCustomerCommand(
//        String email,
//        String firstName,
//        String lastName,
//        String phoneNumber
//) {
//    public CreateCustomerCommand {
//        if (email == null || email.isBlank()) {
//            throw new IllegalArgumentException("Email is required");
//        }
//        if (firstName == null || firstName.isBlank()) {
//            throw new IllegalArgumentException("First name is required");
//        }
//        if (lastName == null || lastName.isBlank()) {
//            throw new IllegalArgumentException("Last name is required");
//        }
//    }
//}

public record CreateCustomerCommand(
        String authUserId,
        String email,
        String firstName,
        String lastName,
        String phoneNumber
) {
    public CreateCustomerCommand {
        if (authUserId == null || authUserId.isBlank()) {
            throw new IllegalArgumentException("authUserId is required");
        }
        if (email == null || email.isBlank()) throw new IllegalArgumentException("Email is required");
        if (firstName == null || firstName.isBlank()) throw new IllegalArgumentException("First name is required");
        if (lastName == null || lastName.isBlank()) throw new IllegalArgumentException("Last name is required");
    }
}

