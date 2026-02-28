package com.svrmslk.tenant.repository;

import com.svrmslk.tenant.domain.model.Tenant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class TenantRepositoryTest {

    @Autowired
    private TenantRepository tenantRepository;

    private Tenant testTenant;

    @BeforeEach
    void setUp() {
        tenantRepository.deleteAll();

        testTenant = Tenant.builder()
                .name("Test Tenant")
                .slug("test-tenant")
                .companyName("Test Company")
                .companyEmail("test@test.com")
                .status(Tenant.TenantStatus.ACTIVE)
                .build();
    }

    @Test
    void save_Success() {
        Tenant saved = tenantRepository.save(testTenant);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTenantId()).isNotNull();
        assertThat(saved.getSlug()).isEqualTo("test-tenant");
    }

    @Test
    void findByTenantId_Success() {
        Tenant saved = tenantRepository.save(testTenant);

        Optional<Tenant> found = tenantRepository.findByTenantId(saved.getTenantId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test Tenant");
    }

    @Test
    void findBySlug_Success() {
        tenantRepository.save(testTenant);

        Optional<Tenant> found = tenantRepository.findBySlug("test-tenant");

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test Tenant");
    }

    @Test
    void existsBySlug_True() {
        tenantRepository.save(testTenant);

        boolean exists = tenantRepository.existsBySlug("test-tenant");

        assertThat(exists).isTrue();
    }

    @Test
    void existsBySlug_False() {
        boolean exists = tenantRepository.existsBySlug("non-existent");

        assertThat(exists).isFalse();
    }

    @Test
    void countByStatus_Success() {
        tenantRepository.save(testTenant);

        Tenant anotherTenant = Tenant.builder()
                .name("Another Tenant")
                .slug("another-tenant")
                .companyName("Another Company")
                .companyEmail("another@test.com")
                .status(Tenant.TenantStatus.ACTIVE)
                .build();
        tenantRepository.save(anotherTenant);

        long count = tenantRepository.countByStatus(Tenant.TenantStatus.ACTIVE);

        assertThat(count).isEqualTo(2);
    }
}
