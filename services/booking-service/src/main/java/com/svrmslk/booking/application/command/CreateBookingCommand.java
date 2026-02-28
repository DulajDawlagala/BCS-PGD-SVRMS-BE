package com.svrmslk.booking.application.command;

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
public class CreateBookingCommand {
    private UUID vehicleId;
    private UUID customerId;
    private UUID companyId;
    private Instant startTime;
    private Instant endTime;
    private BigDecimal price;
}