//// ==========================================
//// FILE: repository/TenantConfigurationRepository.java
//// ==========================================
//package com.svrmslk.tenant.repository;
//
//import com.svrmslk.tenant.entity.TenantConfiguration;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//import java.util.List;
//import java.util.Optional;
//
//@Repository
//public interface TenantConfigurationRepository extends JpaRepository<TenantConfiguration, Long> {
//
//    Optional<TenantConfiguration> findByTenantIdAndConfigKey(String tenantId, String configKey);
//
//    List<TenantConfiguration> findByTenantId(String tenantId);
//
//    void deleteByTenantIdAndConfigKey(String tenantId, String configKey);
//}
package com.svrmslk.tenant.repository;

import com.svrmslk.tenant.domain.model.TenantConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TenantConfigurationRepository
        extends JpaRepository<TenantConfiguration, Long> {

    Optional<TenantConfiguration> findByTenantId(String tenantId);
}
