//package com.svrmslk.booking.dashboard.infrastructure;
//
//import com.svrmslk.booking.dashboard.domain.VehiclePerformance;
//import com.svrmslk.booking.domain.BookingStatus;
//import com.svrmslk.booking.infrastructure.persistence.BookingJpaRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Repository;
//
//import java.math.BigDecimal;
//import java.time.Instant;
//import java.time.temporal.ChronoUnit;
//import java.util.List;
//import java.util.UUID;
//
//@Repository
//@RequiredArgsConstructor
//public class AnalyticsRepository {
//
//    private final BookingJpaRepository bookingRepository;
//    private final JdbcTemplate jdbcTemplate;
//
//    public long countActiveBookingsByCompany(UUID companyId, UUID tenantId) {
//        return bookingRepository.countByCompanyIdAndTenantIdAndStatusIn(
//                companyId,
//                tenantId,
//                List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED)
//        );
//    }
//
//    public BigDecimal calculateMonthlyRevenue(UUID companyId, UUID tenantId) {
//        Instant monthAgo = Instant.now().minus(30, ChronoUnit.DAYS);
//        BigDecimal revenue = bookingRepository.sumRevenueByCompanyIdAndTenantIdSinceDate(companyId, tenantId, monthAgo);
//        return revenue != null ? revenue : BigDecimal.ZERO;
//    }
//
//    public List<VehiclePerformance> getVehiclePerformance(UUID companyId, UUID tenantId) {
//        String sql = """
//            SELECT
//                vehicle_id,
//                COUNT(*) as total_bookings,
//                COALESCE(SUM(price), 0) as total_revenue,
//                CASE
//                    WHEN COUNT(*) > 0 THEN
//                        (COUNT(CASE WHEN status IN ('CONFIRMED', 'COMPLETED') THEN 1 END)::DECIMAL / COUNT(*)::DECIMAL)
//                    ELSE 0
//                END as utilization_rate
//            FROM bookings
//            WHERE company_id = ? AND tenant_id = ?
//            GROUP BY vehicle_id
//            ORDER BY total_revenue DESC
//            """;
//
//        return jdbcTemplate.query(sql, (rs, rowNum) ->
//                        VehiclePerformance.builder()
//                                .vehicleId(UUID.fromString(rs.getString("vehicle_id")))
//                                .totalBookings(rs.getInt("total_bookings"))
//                                .totalRevenue(rs.getBigDecimal("total_revenue"))
//                                .utilizationRate(rs.getDouble("utilization_rate"))
//                                .build(),
//                companyId, tenantId
//        );
//    }
//
//    public long countTotalBookingsByCustomer(UUID customerId, UUID tenantId) {
//        return jdbcTemplate.queryForObject(
//                "SELECT COUNT(*) FROM bookings WHERE customer_id = ? AND tenant_id = ?",
//                Long.class,
//                customerId, tenantId
//        );
//    }
//
//    public BigDecimal calculateTotalSpentByCustomer(UUID customerId, UUID tenantId) {
//        BigDecimal total = jdbcTemplate.queryForObject(
//                "SELECT COALESCE(SUM(price), 0) FROM bookings WHERE customer_id = ? AND tenant_id = ?",
//                BigDecimal.class,
//                customerId, tenantId
//        );
//        return total != null ? total : BigDecimal.ZERO;
//    }
//}

package com.svrmslk.booking.dashboard.infrastructure;

import com.svrmslk.booking.dashboard.domain.VehiclePerformance;
import com.svrmslk.booking.domain.BookingStatus;
import com.svrmslk.booking.infrastructure.persistence.BookingJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class AnalyticsRepository {

    private final BookingJpaRepository bookingRepository;
    private final JdbcTemplate jdbcTemplate;

    public long countActiveBookingsByCompany(UUID companyId, UUID tenantId) {
        return bookingRepository.countByCompanyIdAndTenantIdAndStatusIn(
                companyId,
                tenantId,
                List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED)
        );
    }

    public BigDecimal calculateMonthlyRevenue(UUID companyId, UUID tenantId) {
        Instant monthAgo = Instant.now().minus(30, ChronoUnit.DAYS);
        BigDecimal revenue = bookingRepository.sumRevenueByCompanyIdAndTenantIdSinceDate(companyId, tenantId, monthAgo);
        return revenue != null ? revenue : BigDecimal.ZERO;
    }

    public List<VehiclePerformance> getVehiclePerformance(UUID companyId, UUID tenantId) {
        String sql = """
            SELECT 
                vehicle_id,
                COUNT(*) as total_bookings,
                COALESCE(SUM(price), 0) as total_revenue,
                CASE 
                    WHEN COUNT(*) > 0 THEN 
                        (COUNT(CASE WHEN status IN ('CONFIRMED', 'COMPLETED') THEN 1 END)::DECIMAL / COUNT(*)::DECIMAL)
                    ELSE 0 
                END as utilization_rate
            FROM bookings
            WHERE company_id = ? AND tenant_id = ?
            GROUP BY vehicle_id
            ORDER BY total_revenue DESC
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) ->
                        VehiclePerformance.builder()
                                .vehicleId(UUID.fromString(rs.getString("vehicle_id")))
                                .totalBookings(rs.getInt("total_bookings"))
                                .totalRevenue(rs.getBigDecimal("total_revenue"))
                                .utilizationRate(rs.getDouble("utilization_rate"))
                                .build(),
                companyId, tenantId
        );
    }

    // --- TENANT SPECIFIC METHODS (For Company Views) ---

    public long countTotalBookingsByCustomer(UUID customerId, UUID tenantId) {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM bookings WHERE customer_id = ? AND tenant_id = ?",
                Long.class,
                customerId, tenantId
        );
    }

    public BigDecimal calculateTotalSpentByCustomer(UUID customerId, UUID tenantId) {
        BigDecimal total = jdbcTemplate.queryForObject(
                "SELECT COALESCE(SUM(price), 0) FROM bookings WHERE customer_id = ? AND tenant_id = ?",
                BigDecimal.class,
                customerId, tenantId
        );
        return total != null ? total : BigDecimal.ZERO;
    }

    // --- GLOBAL METHODS (For Customer Dashboard / Me Endpoint) ---

    public long countTotalBookingsByCustomer(UUID customerId) {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM bookings WHERE customer_id = ?",
                Long.class,
                customerId
        );
    }

    public BigDecimal calculateTotalSpentByCustomer(UUID customerId) {
        BigDecimal total = jdbcTemplate.queryForObject(
                "SELECT COALESCE(SUM(price), 0) FROM bookings WHERE customer_id = ?",
                BigDecimal.class,
                customerId
        );
        return total != null ? total : BigDecimal.ZERO;
    }
}