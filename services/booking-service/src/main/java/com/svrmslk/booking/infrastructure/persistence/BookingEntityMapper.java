package com.svrmslk.booking.infrastructure.persistence;

import com.svrmslk.booking.domain.Booking;
import com.svrmslk.booking.shared.domain.BookingId;
import com.svrmslk.booking.shared.domain.CompanyId;
import com.svrmslk.booking.shared.domain.CustomerId;
import com.svrmslk.booking.shared.domain.VehicleId;
import org.springframework.stereotype.Component;

@Component
public class BookingEntityMapper {

    public BookingEntity toEntity(Booking booking) {
        return BookingEntity.builder()
                .id(booking.getId().value())
                .vehicleId(booking.getVehicleId().value())
                .customerId(booking.getCustomerId().value())
                .companyId(booking.getCompanyId().value())
                .tenantId(booking.getTenantId())
                .startTime(booking.getStartTime())
                .endTime(booking.getEndTime())
                .status(booking.getStatus())
                .price(booking.getPrice())
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                .build();
    }

    public Booking toDomain(BookingEntity entity) {
        return Booking.builder()
                .id(new BookingId(entity.getId()))
                .vehicleId(VehicleId.of(entity.getVehicleId()))
                .customerId(CustomerId.of(entity.getCustomerId()))
                .companyId(CompanyId.of(entity.getCompanyId()))
                .tenantId(entity.getTenantId())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .status(entity.getStatus())
                .price(entity.getPrice())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}