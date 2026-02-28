// FILE: profile/infrastructure/persistence/CustomerProfileJpaRepository.java
package com.svrmslk.customer.profile.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CustomerProfileJpaRepository extends JpaRepository<CustomerProfileEntity, Long> {

    Optional<CustomerProfileEntity> findByCustomerId(String customerId);
}