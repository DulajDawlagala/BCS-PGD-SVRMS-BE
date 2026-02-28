// FILE: profile/presentation/mapper/CustomerDtoMapper.java
package com.svrmslk.customer.profile.presentation.mapper;

import com.svrmslk.customer.profile.application.command.CreateCustomerCommand;
import com.svrmslk.customer.profile.application.command.UpdateCustomerCommand;
import com.svrmslk.customer.profile.domain.Customer;
import com.svrmslk.customer.profile.domain.CustomerProfile;
import com.svrmslk.customer.profile.presentation.dto.CreateCustomerRequest;
import com.svrmslk.customer.profile.presentation.dto.CustomerProfileResponse;
import com.svrmslk.customer.profile.presentation.dto.UpdateCustomerProfileRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomerDtoMapper {

    CreateCustomerCommand toCommand(CreateCustomerRequest request);

    @Mapping(target = "customerId", source = "customerId")
    UpdateCustomerCommand toCommand(String customerId, UpdateCustomerProfileRequest request);

    @Mapping(target = "customerId", expression = "java(customer.getId().getValue())")
    @Mapping(target = "email", expression = "java(customer.getEmail().getValue())")
    @Mapping(target = "firstName", source = "profile.firstName")
    @Mapping(target = "lastName", source = "profile.lastName")
    @Mapping(target = "phoneNumber", source = "profile.phoneNumber")
    @Mapping(target = "dateOfBirth", source = "profile.dateOfBirth")
    @Mapping(target = "nationality", source = "profile.nationality")
    @Mapping(target = "driversLicenseNumber", source = "profile.driversLicenseNumber")
    @Mapping(target = "licenseExpiryDate", source = "profile.licenseExpiryDate")
    @Mapping(target = "preferences", source = "profile.preferences")
    CustomerProfileResponse toResponse(Customer customer, CustomerProfile profile);
    @Mapping(target = "authUserId", source = "authUserId")
    CreateCustomerCommand toCommand(CreateCustomerRequest request, String authUserId);

}
