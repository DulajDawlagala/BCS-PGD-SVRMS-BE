// FILE: profile/infrastructure/persistence/CustomerProfileRepository.java
package com.svrmslk.customer.profile.infrastructure.persistence;

import com.svrmslk.customer.profile.domain.CustomerProfile;
import com.svrmslk.customer.shared.domain.CustomerId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomerProfileRepository {

    private final CustomerProfileJpaRepository jpaRepository;
    private final CustomerProfileEntityMapper mapper;

    public CustomerProfile save(CustomerProfile profile) {
        CustomerProfileEntity entity = mapper.toEntity(profile);
        entity = jpaRepository.save(entity);
        return mapper.toDomain(entity);
    }

    public Optional<CustomerProfile> findByCustomerId(CustomerId customerId) {
        return jpaRepository.findByCustomerId(customerId.getValue())
                .map(mapper::toDomain);
    }
}
