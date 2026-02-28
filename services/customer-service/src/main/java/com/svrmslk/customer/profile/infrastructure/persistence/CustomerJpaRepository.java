//// FILE: profile/infrastructure/persistence/CustomerJpaRepository.java
//package com.svrmslk.customer.profile.infrastructure.persistence;
//
//import com.svrmslk.customer.shared.domain.Email;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//import java.util.Optional;
//
//@Repository
//public interface CustomerJpaRepository extends JpaRepository<CustomerEntity, String> {
//
//    Optional<CustomerEntity> findByEmail(String email);
//
//    boolean existsByEmail(String email);
//}

// FILE: profile/infrastructure/persistence/CustomerJpaRepository.java
package com.svrmslk.customer.profile.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerJpaRepository extends JpaRepository<CustomerEntity, String> {

    /* =========================
       Lookup
       ========================= */

    Optional<CustomerEntity> findByEmail(String email);

    Optional<CustomerEntity> findByAuthUserId(String authUserId);


    /* =========================
       Existence checks
       ========================= */

    boolean existsByEmail(String email);

    boolean existsByAuthUserId(String authUserId);
}
