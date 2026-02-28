//package com.svrmslk.tenant;
//
//import com.svrmslk.tenant.presentation.dto.TenantRequest;
//import com.svrmslk.tenant.presentation.dto.TenantResponse;
//import com.svrmslk.tenant.domain.model.Tenant;
//import com.svrmslk.tenant.repository.TenantRepository;
//import com.svrmslk.tenant.application.service.TenantService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.transaction.annotation.Transactional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//
//@SpringBootTest
//@ActiveProfiles("test")
//@Transactional
//class TenantServiceApplicationTests {
//
//    @Autowired
//    private TenantService tenantService;
//
//    @Autowired
//    private TenantRepository tenantRepository;
//
//    @BeforeEach
//    void setUp() {
//        tenantRepository.deleteAll();
//    }
//
//    @Test
//    void contextLoads() {
//        assertThat(tenantService).isNotNull();
//    }
//
//    @Test
//    void testCreateTenant() {
//        TenantRequest request = TenantRequest.builder()
//                .name("Test Rental Company")
//                .slug("test-rental")
//                .companyName("Test Rental Inc.")
//                .companyEmail("test@testrental.com")
//                .companyPhone("+1234567890")
//                .maxUsers(50)
//                .maxBranches(5)
//                .maxVehicles(100)
//                .build();
//
//        TenantResponse response = tenantService.createTenant(request);
//
//        assertThat(response).isNotNull();
//        assertThat(response.getTenantId()).isNotNull();
//        assertThat(response.getName()).isEqualTo("Test Rental Company");
//        assertThat(response.getSlug()).isEqualTo("test-rental");
//        assertThat(response.getStatus()).isEqualTo(Tenant.TenantStatus.PENDING);
//        assertThat(response.getDatabaseSchema()).isEqualTo("tenant_test-rental");
//    }
//
//    @Test
//    void testGetTenant() {
//        TenantRequest request = TenantRequest.builder()
//                .name("Test Company")
//                .slug("test-company")
//                .companyName("Test Company Inc.")
//                .companyEmail("info@testcompany.com")
//                .build();
//
//        TenantResponse created = tenantService.createTenant(request);
//        TenantResponse retrieved = tenantService.getTenant(created.getTenantId());
//
//        assertThat(retrieved).isNotNull();
//        assertThat(retrieved.getTenantId()).isEqualTo(created.getTenantId());
//        assertThat(retrieved.getName()).isEqualTo("Test Company");
//    }
//
//    @Test
//    void testActivateTenant() {
//        TenantRequest request = TenantRequest.builder()
//                .name("Activation Test")
//                .slug("activation-test")
//                .companyName("Activation Test Inc.")
//                .companyEmail("info@activationtest.com")
//                .build();
//
//        TenantResponse created = tenantService.createTenant(request);
//        tenantService.activateTenant(created.getTenantId());
//
//        TenantResponse activated = tenantService.getTenant(created.getTenantId());
//        assertThat(activated.getStatus()).isEqualTo(Tenant.TenantStatus.ACTIVE);
//        assertThat(activated.getActivatedAt()).isNotNull();
//    }
//
//    @Test
//    void testValidateTenant() {
//        TenantRequest request = TenantRequest.builder()
//                .name("Validation Test")
//                .slug("validation-test")
//                .companyName("Validation Test Inc.")
//                .companyEmail("info@validationtest.com")
//                .build();
//
//        TenantResponse created = tenantService.createTenant(request);
//        tenantService.activateTenant(created.getTenantId());
//
//        boolean isValid = tenantService.validateTenant(created.getTenantId());
//        assertThat(isValid).isTrue();
//    }
//
//    @Test
//    void testDuplicateSlugThrowsException() {
//        TenantRequest request1 = TenantRequest.builder()
//                .name("Test 1")
//                .slug("duplicate-slug")
//                .companyName("Test 1 Inc.")
//                .companyEmail("test1@test.com")
//                .build();
//
//        TenantRequest request2 = TenantRequest.builder()
//                .name("Test 2")
//                .slug("duplicate-slug")
//                .companyName("Test 2 Inc.")
//                .companyEmail("test2@test.com")
//                .build();
//
//        tenantService.createTenant(request1);
//
//        assertThrows(Exception.class, () -> tenantService.createTenant(request2));
//    }
//}

package com.svrmslk.tenant;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TenantServiceApplicationTests {

    @Test
    void contextLoads() {
    }
}
