package com.svrmslk.company.vehicle.infrastructure.persistence;

import com.svrmslk.company.vehicle.domain.VehicleStatus;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "vehicles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class VehicleEntity {

    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(name = "company_id", nullable = false, columnDefinition = "UUID")
    private UUID companyId;

    @Column(name = "tenant_id", nullable = false, columnDefinition = "UUID")
    private UUID tenantId;

    @Column(nullable = false)
    private String make;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private Integer year;

    private String color;

    @Column(name = "license_plate", unique = true)
    private String licensePlate;

    @Column(unique = true)
    private String vin;

    @Type(JsonType.class)
    @Column(name = "image_urls", columnDefinition = "jsonb")
    private List<String> imageUrls;

    @Column(name = "registration_doc_url")
    private String registrationDocUrl;

    @Column(name = "insurance_doc_url")
    private String insuranceDocUrl;

    @Column(name = "hourly_rate", precision = 10, scale = 2)
    private BigDecimal hourlyRate;

    @Column(name = "daily_rate", precision = 10, scale = 2)
    private BigDecimal dailyRate;

    @Column(name = "weekly_rate", precision = 10, scale = 2)
    private BigDecimal weeklyRate;

    @Column(name = "monthly_rate", precision = 10, scale = 2)
    private BigDecimal monthlyRate;

    @Type(JsonType.class)
    @Column(name = "available_days", columnDefinition = "jsonb")
    private List<String> availableDays;

    @Column(name = "min_rental_hours")
    private Integer minRentalHours;

    @Column(name = "max_rental_days")
    private Integer maxRentalDays;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleStatus status;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}