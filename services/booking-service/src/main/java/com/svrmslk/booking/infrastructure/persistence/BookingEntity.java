package com.svrmslk.booking.infrastructure.persistence;

import com.svrmslk.booking.domain.BookingStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "bookings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class BookingEntity {

    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(name = "vehicle_id", nullable = false, columnDefinition = "UUID")
    private UUID vehicleId;

    @Column(name = "customer_id", nullable = false, columnDefinition = "UUID")
    private UUID customerId;

    @Column(name = "company_id", nullable = false, columnDefinition = "UUID")
    private UUID companyId;

    @Column(name = "tenant_id", nullable = false, columnDefinition = "UUID")
    private UUID tenantId;

    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @Column(name = "end_time", nullable = false)
    private Instant endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal price;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}