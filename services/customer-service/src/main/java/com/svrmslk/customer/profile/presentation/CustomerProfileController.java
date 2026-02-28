// FILE: profile/presentation/CustomerProfileController.java
package com.svrmslk.customer.profile.presentation;

import com.svrmslk.customer.profile.application.CustomerProfileService;
import com.svrmslk.customer.profile.application.command.CreateCustomerCommand;
import com.svrmslk.customer.profile.domain.Customer;
import com.svrmslk.customer.profile.domain.CustomerProfile;
import com.svrmslk.customer.profile.presentation.dto.CreateCustomerRequest;
import com.svrmslk.customer.profile.presentation.dto.CustomerProfileResponse;
import com.svrmslk.customer.profile.presentation.mapper.CustomerDtoMapper;
import com.svrmslk.customer.shared.api.ApiResponse;
import com.svrmslk.customer.shared.domain.CustomerId;
import com.svrmslk.customer.shared.security.CustomerSecurityContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Customer Profile")
@SecurityRequirement(name = "Bearer Authentication")
public class CustomerProfileController {

    private final CustomerProfileService profileService;
    private final CustomerDtoMapper mapper;
    private final CustomerSecurityContext securityContext;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<String>> createCustomer(
            @Valid @RequestBody CreateCustomerRequest request) {

        // Get user ID and email from API Gateway headers
        String authUserId = securityContext.getAuthUserId();
        String authEmail = securityContext.getAuthEmail();

        log.info("Creating customer - authUserId: {}, authEmail: {}", authUserId, authEmail);

        if (!request.email().equalsIgnoreCase(authEmail)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(
                            "Email does not match authenticated user",
                            HttpStatus.FORBIDDEN.value()
                    ));
        }

        CreateCustomerCommand command = mapper.toCommand(request, authUserId);
        CustomerId customerId = profileService.createCustomer(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Customer created successfully",
                        customerId.getValue()
                ));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<CustomerProfileResponse>> getCurrentProfile() {

        // Get user ID from API Gateway headers
        String authUserId = securityContext.getAuthUserId();

        log.info("Fetching profile for authUserId: {}", authUserId);

        Customer customer = profileService.getByAuthUserId(authUserId);
        CustomerProfile profile = profileService.getProfile(customer.getId());

        return ResponseEntity.ok(
                ApiResponse.success(mapper.toResponse(customer, profile))
        );
    }
    // ✅ NEW: Update customer profile
//    @PutMapping("/me")
//    @PreAuthorize("hasRole('CUSTOMER')")
//    public ResponseEntity<ApiResponse<CustomerProfileResponse>> updateProfile(
//            @Valid @RequestBody UpdateCustomerRequest request) {
//
//        String authUserId = securityContext.getAuthUserId();
//        log.info("Updating profile for authUserId: {}", authUserId);
//
//        // Fetch existing customer
//        Customer customer = profileService.getByAuthUserId(authUserId);
//
//        // Map request to update command
//        UpdateCustomerCommand command = mapper.toUpdateCommand(customer.getId(), request);
//        CustomerProfile updatedProfile = profileService.updateProfile(command);
//
//        return ResponseEntity.ok(ApiResponse.success(
//                mapper.toResponse(customer, updatedProfile)
//        ));
//    }
}
