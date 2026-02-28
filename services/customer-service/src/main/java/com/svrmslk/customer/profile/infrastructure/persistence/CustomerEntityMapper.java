// FILE: profile/infrastructure/persistence/CustomerEntityMapper.java
package com.svrmslk.customer.profile.infrastructure.persistence;

import com.svrmslk.customer.profile.domain.Customer;
import com.svrmslk.customer.shared.domain.CustomerId;
import com.svrmslk.customer.shared.domain.Email;
import org.springframework.stereotype.Component;

@Component
public class CustomerEntityMapper {

    /**
     * Maps a domain Customer to a JPA entity for persistence.
     */
    public CustomerEntity toEntity(Customer customer) {
        if (customer == null) {
            return null;
        }

        CustomerEntity entity = new CustomerEntity();
        entity.setCustomerId(customer.getId().getValue());
        entity.setEmail(customer.getEmail().getValue());
        entity.setStatus(customer.getStatus());
        entity.setRegisteredAt(customer.getRegisteredAt());
        entity.setLastLoginAt(customer.getLastLoginAt());
        entity.setAuthUserId(customer.getAuthUserId()); // critical fix
        return entity;
    }

    /**
     * Maps a JPA entity to a domain Customer.
     * Includes authUserId to ensure the domain object is fully reconstructed.
     */
    public Customer toDomain(CustomerEntity entity) {
        if (entity == null) {
            return null;
        }

        return Customer.rehydrate(
                CustomerId.of(entity.getCustomerId()),
                Email.of(entity.getEmail()),
                entity.getAuthUserId(),          // pass authUserId properly
                entity.getStatus(),
                entity.getRegisteredAt(),
                entity.getLastLoginAt()
        );
    }
}

