package com.svrmslk.company.dashboard.domain;

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
public class RecentBooking {
    private UUID bookingId;
    private String vehicleName;
    private String customerName;
    private Instant startDate;
    private Instant endDate;
    private BigDecimal totalAmount;
    private String status;
}