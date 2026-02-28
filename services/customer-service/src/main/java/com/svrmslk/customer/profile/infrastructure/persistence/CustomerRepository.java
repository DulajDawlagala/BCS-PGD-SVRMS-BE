// FILE: profile/infrastructure/persistence/CustomerRepository.java
package com.svrmslk.customer.profile.infrastructure.persistence;

import com.svrmslk.customer.profile.domain.Customer;
import com.svrmslk.customer.shared.domain.CustomerId;
import com.svrmslk.customer.shared.domain.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomerRepository {

    private final CustomerJpaRepository jpaRepository;
    private final CustomerEntityMapper mapper;

    public Customer save(Customer customer) {
        CustomerEntity entity = mapper.toEntity(customer);
        entity = jpaRepository.save(entity);
        return mapper.toDomain(entity);
    }

    public Optional<Customer> findById(CustomerId customerId) {
        return jpaRepository.findById(customerId.getValue())
                .map(mapper::toDomain);
    }

    public Optional<Customer> findByEmail(Email email) {
        return jpaRepository.findByEmail(email.getValue())
                .map(mapper::toDomain);
    }

    public boolean existsByAuthUserId(String authUserId) {
        return jpaRepository.existsByAuthUserId(authUserId);
    }

    public Optional<Customer> findByAuthUserId(String authUserId) {
        return jpaRepository.findByAuthUserId(authUserId)
                .map(mapper::toDomain);
    }

    public boolean existsByEmail(Email email) {
        return jpaRepository.existsByEmail(email.getValue());
    }
}
