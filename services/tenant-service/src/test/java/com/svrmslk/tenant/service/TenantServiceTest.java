//package com.svrmslk.tenant.application.service;
//
//import com.svrmslk.tenant.presentation.dto.TenantRequest;
//import com.svrmslk.tenant.presentation.dto.TenantResponse;
//import com.svrmslk.tenant.domain.model.Tenant;
//import com.svrmslk.tenant.domain.exception.TenantNotFoundException;
//import com.svrmslk.tenant.application.mapper.TenantMapper;
//import com.svrmslk.tenant.infrastructure.repository.TenantRepository;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.kafka.core.KafkaTemplate;
//
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class TenantServiceTest {
//
//    @Mock
//    TenantRepository tenantRepository;
//
//    @Mock
//    TenantMapper tenantMapper;
//
//    @Mock
//    TenantProvisioningService provisioningService;
//
//    @Mock
//    KafkaTemplate<String, Object> kafkaTemplate;
//
//    @InjectMocks
//    TenantService tenantService;
//
//    private Tenant testTenant;
//    private TenantRequest testRequest;
//    private TenantResponse testResponse;
//
//    @BeforeEach
//    void setUp() {
//        testTenant = Tenant.builder()
//                .id(1L)
//                .tenantId("test-tenant-id")
//                .name("Test Tenant")
//                .slug("test-tenant")
//                .companyName("Test Company")
//                .companyEmail("test@test.com")
//                .status(Tenant.TenantStatus.PENDING)
//                .build();
//
//        testRequest = TenantRequest.builder()
//                .name("Test Tenant")
//                .slug("test-tenant")
//                .companyName("Test Company")
//                .companyEmail("test@test.com")
//                .build();
//
//        testResponse = TenantResponse.builder()
//                .id(1L)
//                .tenantId("test-tenant-id")
//                .name("Test Tenant")
//                .slug("test-tenant")
//                .build();
//    }
//
//    @Test
//    void createTenant_Success() {
//        when(tenantRepository.existsBySlug(anyString())).thenReturn(false);
//        when(tenantMapper.toEntity(any())).thenReturn(testTenant);
//        when(tenantRepository.save(any())).thenReturn(testTenant);
//        when(tenantMapper.toResponse(any())).thenReturn(testResponse);
//        doNothing().when(provisioningService).provisionTenant(any());
//
//        TenantResponse result = tenantService.createTenant(testRequest);
//
//        assertThat(result).isNotNull();
//        assertThat(result.getTenantId()).isEqualTo("test-tenant-id");
//        verify(tenantRepository).save(any());
//        verify(provisioningService).provisionTenant(any());
//    }
//
//    @Test
//    void getTenant_Success() {
//        when(tenantRepository.findByTenantId(anyString()))
//                .thenReturn(Optional.of(testTenant));
//        when(tenantMapper.toResponse(any()))
//                .thenReturn(testResponse);
//
//        TenantResponse result = tenantService.getTenant("test-tenant-id");
//
//        assertThat(result.getTenantId()).isEqualTo("test-tenant-id");
//    }
//
//    @Test
//    void getTenant_NotFound() {
//        when(tenantRepository.findByTenantId(anyString()))
//                .thenReturn(Optional.empty());
//
//        assertThrows(TenantNotFoundException.class,
//                () -> tenantService.getTenant("missing"));
//    }
//
//    @Test
//    void activateTenant_Success() {
//        when(tenantRepository.findByTenantId(anyString()))
//                .thenReturn(Optional.of(testTenant));
//
//        tenantService.activateTenant("test-tenant-id");
//
//        assertThat(testTenant.getStatus())
//                .isEqualTo(Tenant.TenantStatus.ACTIVE);
//        verify(tenantRepository).save(any());
//    }
//
//    @Test
//    void validateTenant_Active() {
//        testTenant.setStatus(Tenant.TenantStatus.ACTIVE);
//        when(tenantRepository.findByTenantId(anyString()))
//                .thenReturn(Optional.of(testTenant));
//
//        assertThat(tenantService.validateTenant("id")).isTrue();
//    }
//
//    @Test
//    void validateTenant_Inactive() {
//        testTenant.setStatus(Tenant.TenantStatus.SUSPENDED);
//        when(tenantRepository.findByTenantId(anyString()))
//                .thenReturn(Optional.of(testTenant));
//
//        assertThat(tenantService.validateTenant("id")).isFalse();
//    }
//}

package com.svrmslk.tenant.service;

import com.svrmslk.tenant.application.service.TenantService;
import com.svrmslk.tenant.domain.model.Tenant;
import com.svrmslk.tenant.repository.TenantRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
//import static org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class TenantServiceTest {

    @Mock
    TenantRepository tenantRepository;

    @InjectMocks
    TenantService tenantService;

    @Test
    void shouldReturnAllTenants() {
        when(tenantRepository.findAll()).thenReturn(List.of(new Tenant()));

        List<Tenant> tenants = tenantService.findAll();

        assertThat(tenants).hasSize(1);
    }

    @Test
    void shouldReturnTenantById() {
        Tenant tenant = new Tenant();
        when(tenantRepository.findById(1L)).thenReturn(Optional.of(tenant));

        Tenant result = tenantService.findById(1L);

        assertThat(result).isNotNull();
    }
}
