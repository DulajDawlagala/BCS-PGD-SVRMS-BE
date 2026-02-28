//// FILE: profile/infrastructure/persistence/CustomerProfileEntityMapper.java
//package com.svrmslk.customer.profile.infrastructure.persistence;
//
//import com.svrmslk.customer.profile.domain.CustomerProfile;
//import com.svrmslk.customer.shared.domain.CustomerId;
//import org.springframework.stereotype.Component;
//
//@Component
//public class CustomerProfileEntityMapper {
//
//    public CustomerProfileEntity toEntity(CustomerProfile profile) {
//        CustomerProfileEntity entity = new CustomerProfileEntity();
//        entity.setCustomerId(profile.getCustomerId().getValue());
//        entity.setFirstName(profile.getFirstName());
//        entity.setLastName(profile.getLastName());
//        entity.setPhoneNumber(profile.getPhoneNumber());
//        entity.setDateOfBirth(profile.getDateOfBirth());
//        entity.setNationality(profile.getNationality());
//        entity.setDriversLicenseNumber(profile.getDriversLicenseNumber());
//        entity.setLicenseExpiryDate(profile.getLicenseExpiryDate());
//        entity.setPreferences(profile.getPreferences());
//        return entity;
//    }
//
//    public CustomerProfile toDomain(CustomerProfileEntity entity) {
//        CustomerProfile profile = CustomerProfile.createDefault(
//                CustomerId.of(entity.getCustomerId())
//        );
//        profile.setFirstName(entity.getFirstName());
//        profile.setLastName(entity.getLastName());
//        profile.setPhoneNumber(entity.getPhoneNumber());
//        profile.setDateOfBirth(entity.getDateOfBirth());
//        profile.setNationality(entity.getNationality());
//        profile.setDriversLicenseNumber(entity.getDriversLicenseNumber());
//        profile.setLicenseExpiryDate(entity.getLicenseExpiryDate());
//        profile.setPreferences(entity.getPreferences());
//        return profile;
//    }
//}

// FILE: profile/infrastructure/persistence/CustomerProfileEntityMapper.java
package com.svrmslk.customer.profile.infrastructure.persistence;

import com.svrmslk.customer.profile.domain.CustomerProfile;
import com.svrmslk.customer.shared.domain.CustomerId;
import org.springframework.stereotype.Component;

@Component
public class CustomerProfileEntityMapper {

    public CustomerProfileEntity toEntity(CustomerProfile profile) {
        CustomerProfileEntity entity = new CustomerProfileEntity();

        entity.setCustomerId(profile.getCustomerId().getValue());
        entity.setFirstName(profile.getFirstName());
        entity.setLastName(profile.getLastName());
        entity.setPhoneNumber(profile.getPhoneNumber());
        entity.setDateOfBirth(profile.getDateOfBirth());
        entity.setNationality(profile.getNationality());
        entity.setDriversLicenseNumber(profile.getDriversLicenseNumber());
        entity.setLicenseExpiryDate(profile.getLicenseExpiryDate());
        entity.setPreferences(profile.getPreferences());

        return entity;
    }

    public CustomerProfile toDomain(CustomerProfileEntity entity) {
        return CustomerProfile.rehydrate(
                CustomerId.of(entity.getCustomerId()),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getPhoneNumber(),
                entity.getDateOfBirth(),
                entity.getNationality(),
                entity.getDriversLicenseNumber(),
                entity.getLicenseExpiryDate(),
                entity.getPreferences()
        );
    }
}
