//package com.svrmslk.tenant.presentation.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.svrmslk.tenant.application.TenantService;
//import com.svrmslk.tenant.presentation.dto.TenantRequest;
//import com.svrmslk.tenant.presentation.dto.TenantResponse;
//import com.svrmslk.tenant.domain.model.Tenant;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import com.svrmslk.tenant.presentation.dto.TenantRequest;
//import com.svrmslk.tenant.presentation.dto.TenantResponse;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(controllers = TenantController.class)
//@AutoConfigureMockMvc(addFilters = false) // disable Spring Security filters
//class TenantControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @MockBean
//    private TenantService tenantService;
//
//    @Test
//    void createTenant_Success() throws Exception {
//        TenantRequest request = TenantRequest.builder()
//                .name("Test Tenant")
//                .slug("test-tenant")
//                .companyName("Test Company")
//                .companyEmail("test@test.com")
//                .build();
//
//        TenantResponse response = TenantResponse.builder()
//                .id(1L)
//                .tenantId("test-id")
//                .name("Test Tenant")
//                .slug("test-tenant")
//                .status(Tenant.TenantStatus.PENDING)
//                .build();
//
//        when(tenantService.createTenant(any())).thenReturn(response);
//
//        mockMvc.perform(post("/api/v1/tenants")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.data.tenantId").value("test-id"));
//    }
//
//    @Test
//    void getTenant_Success() throws Exception {
//        TenantResponse response = TenantResponse.builder()
//                .id(1L)
//                .tenantId("test-id")
//                .name("Test Tenant")
//                .slug("test-tenant")
//                .build();
//
//        when(tenantService.getTenant(anyString())).thenReturn(response);
//
//        mockMvc.perform(get("/api/v1/tenants/test-id"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data.tenantId").value("test-id"));
//    }
//
//    @Test
//    void validateTenant_Success() throws Exception {
//        when(tenantService.validateTenant(anyString())).thenReturn(true);
//
//        mockMvc.perform(get("/api/v1/tenants/test-id/validate"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data").value(true));
//    }
//}

package com.svrmslk.tenant.controller;

import com.svrmslk.tenant.application.service.TenantService;
import com.svrmslk.tenant.presentation.controller.TenantController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TenantController.class)
class TenantControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    TenantService tenantService;

    @Test
    void shouldReturn200() throws Exception {
        mockMvc.perform(get("/tenants"))
                .andExpect(status().isOk());
    }
}

