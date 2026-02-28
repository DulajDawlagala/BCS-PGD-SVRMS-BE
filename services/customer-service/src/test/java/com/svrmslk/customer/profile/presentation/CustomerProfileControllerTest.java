//// FILE: presentation/CustomerProfileControllerTest.java
//package com.svrmslk.customer.profile.presentation;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.svrmslk.customer.profile.application.CustomerProfileService;
//import com.svrmslk.customer.profile.domain.Customer;
//import com.svrmslk.customer.profile.domain.CustomerProfile;
//import com.svrmslk.customer.profile.presentation.dto.CreateCustomerRequest;
//import com.svrmslk.customer.shared.domain.CustomerId;
//import com.svrmslk.customer.shared.domain.Email;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.web.servlet.MockMvc;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(CustomerProfileController.class)
//class CustomerProfileControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @MockBean
//    private CustomerProfileService profileService;
//
//    @Test
//    void shouldCreateCustomer() throws Exception {
//        // Given
//        CreateCustomerRequest request = new CreateCustomerRequest(
//                "test@example.com",
//                "John",
//                "Doe",
//                "+1234567890"
//        );
//
//        CustomerId customerId = CustomerId.generate();
//        when(profileService.createCustomer(any())).thenReturn(customerId);
//
//        // When/Then
//        mockMvc.perform(post("/api/v1/customers")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.data").value(customerId.getValue()));
//    }
//
//    @Test
//    @WithMockUser(roles = "CUSTOMER")
//    void shouldGetCustomerProfile() throws Exception {
//        // Given
//        CustomerId customerId = CustomerId.generate();
//        Customer customer = Customer.create(Email.of("test@example.com"));
//        customer.setId(customerId);
//        CustomerProfile profile = CustomerProfile.createDefault(customerId);
//
//        when(profileService.getCustomer(any())).thenReturn(customer);
//        when(profileService.getProfile(any())).thenReturn(profile);
//
//        // When/Then
//        mockMvc.perform(get("/api/v1/customers/" + customerId.getValue()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true));
//    }
//}

// FILE: presentation/CustomerProfileControllerTest.java
package com.svrmslk.customer.profile.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svrmslk.customer.profile.application.CustomerProfileService;
import com.svrmslk.customer.profile.domain.Customer;
import com.svrmslk.customer.profile.domain.CustomerProfile;
import com.svrmslk.customer.profile.presentation.dto.CreateCustomerRequest;
import com.svrmslk.customer.shared.domain.CustomerId;
import com.svrmslk.customer.shared.domain.CustomerStatus;
import com.svrmslk.customer.shared.domain.Email;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerProfileController.class)
class CustomerProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomerProfileService profileService;

    @Test
    void shouldCreateCustomer() throws Exception {
        // Given
        CreateCustomerRequest request = new CreateCustomerRequest(
                "test@example.com",
                "John",
                "Doe",
                "+1234567890"
        );

        CustomerId customerId = CustomerId.generate();
        when(profileService.createCustomer(any())).thenReturn(customerId);

        // When / Then
        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(customerId.getValue()));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void shouldGetCustomerProfile() throws Exception {
        // Given
        CustomerId customerId = CustomerId.generate();

        Customer customer = Customer.rehydrate(
                customerId,
                Email.of("test@example.com"),
                "auth-user-123",              // ✅ authUserId
                CustomerStatus.ACTIVE,
                LocalDateTime.now(),
                null
        );


        CustomerProfile profile = CustomerProfile.createDefault(customerId);

        when(profileService.getCustomer(any())).thenReturn(customer);
        when(profileService.getProfile(any())).thenReturn(profile);

        // When / Then
        mockMvc.perform(get("/api/v1/customers/" + customerId.getValue()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
