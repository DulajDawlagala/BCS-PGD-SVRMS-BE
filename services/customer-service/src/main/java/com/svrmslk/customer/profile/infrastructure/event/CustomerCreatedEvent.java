// FILE: profile/infrastructure/event/CustomerCreatedEvent.java
package com.svrmslk.customer.profile.infrastructure.event;

import com.svrmslk.customer.shared.event.DomainEvent;

public class CustomerCreatedEvent extends DomainEvent {

    private static final String EVENT_TYPE = "CustomerCreated";
    private static final String EVENT_VERSION = "v1";

    private final String customerId;
    private final String email;
    private final String firstName;
    private final String lastName;

    public CustomerCreatedEvent(String customerId, String email,
                                String firstName, String lastName) {
        super(EVENT_TYPE, EVENT_VERSION);
        this.customerId = customerId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getCustomerId() { return customerId; }
    public String getEmail() { return email; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
}