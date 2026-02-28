package com.svrmslk.booking.presentation.mapper;

import com.svrmslk.booking.application.command.CreateBookingCommand;
import com.svrmslk.booking.domain.Booking;
import com.svrmslk.booking.presentation.dto.BookingResponse;
import com.svrmslk.booking.presentation.dto.CreateBookingRequest;
import org.springframework.stereotype.Component;

@Component
public class BookingDtoMapper {

    public CreateBookingCommand toCreateCommand(CreateBookingRequest request) {
        return CreateBookingCommand.builder()
                .vehicleId(request.getVehicleId())
                .customerId(request.getCustomerId())
                .companyId(request.getCompanyId())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .price(request.getPrice())
                .build();
    }

    public BookingResponse toResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId().value())
                .vehicleId(booking.getVehicleId().value())
                .customerId(booking.getCustomerId().value())
                .companyId(booking.getCompanyId().value())
                .startTime(booking.getStartTime())
                .endTime(booking.getEndTime())
                .status(booking.getStatus())
                .price(booking.getPrice())
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                .build();
    }
}