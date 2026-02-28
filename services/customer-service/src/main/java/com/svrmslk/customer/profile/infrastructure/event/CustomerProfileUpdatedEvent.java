// FILE: profile/infrastructure/event/CustomerProfileUpdatedEvent.java
package com.svrmslk.customer.profile.infrastructure.event;

import com.svrmslk.customer.shared.event.DomainEvent;

public class CustomerProfileUpdatedEvent extends DomainEvent {

    private static final String EVENT_TYPE = "CustomerProfileUpdated";
    private static final String EVENT_VERSION = "v1";

    private final String customerId;
    private final String firstName;
    private final String lastName;

    public CustomerProfileUpdatedEvent(String customerId, String firstName, String lastName) {
        super(EVENT_TYPE, EVENT_VERSION);
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getCustomerId() { return customerId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
}
