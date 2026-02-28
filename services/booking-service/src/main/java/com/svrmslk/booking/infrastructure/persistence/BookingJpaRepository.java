package com.svrmslk.booking.infrastructure.persistence;

import com.svrmslk.booking.domain.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookingJpaRepository extends JpaRepository<BookingEntity, UUID> {

    List<BookingEntity> findByCustomerIdAndTenantId(UUID customerId, UUID tenantId);

    List<BookingEntity> findByCompanyIdAndTenantId(UUID companyId, UUID tenantId);

    @Query("SELECT COUNT(b) FROM BookingEntity b WHERE b.companyId = :companyId AND b.tenantId = :tenantId AND b.status IN :statuses")
    long countByCompanyIdAndTenantIdAndStatusIn(@Param("companyId") UUID companyId, @Param("tenantId") UUID tenantId, @Param("statuses") List<BookingStatus> statuses);

    @Query("SELECT SUM(b.price) FROM BookingEntity b WHERE b.companyId = :companyId AND b.tenantId = :tenantId AND b.createdAt >= :startDate")
    java.math.BigDecimal sumRevenueByCompanyIdAndTenantIdSinceDate(@Param("companyId") UUID companyId, @Param("tenantId") UUID tenantId, @Param("startDate") java.time.Instant startDate);

    List<BookingEntity> findByVehicleIdAndTenantId(UUID vehicleId, UUID tenantId);

    // ✅ ADD THIS:
    List<BookingEntity> findByCustomerId(UUID customerId);

    long countByCustomerId(UUID customerId);

    long countByCustomerIdAndStatus(UUID customerId, BookingStatus status);

    long countByCustomerIdAndStatusIn(UUID customerId, List<BookingStatus> statuses);
}