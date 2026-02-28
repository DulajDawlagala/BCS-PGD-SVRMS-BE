package com.svrmslk.booking.presentation.dto;

import com.svrmslk.booking.domain.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private UUID id;
    private UUID vehicleId;
    private UUID customerId;
    private UUID companyId;
    private Instant startTime;
    private Instant endTime;
    private BookingStatus status;
    private BigDecimal price;
    private Instant createdAt;
    private Instant updatedAt;
}