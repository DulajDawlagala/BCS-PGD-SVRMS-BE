package com.svrmslk.booking.infrastructure.persistence;

import com.svrmslk.booking.domain.Booking;
import com.svrmslk.booking.shared.domain.BookingId;
import com.svrmslk.booking.shared.domain.CompanyId;
import com.svrmslk.booking.shared.domain.CustomerId;
import com.svrmslk.booking.shared.domain.VehicleId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import com.svrmslk.booking.domain.BookingStatus;

@Repository
@RequiredArgsConstructor
public class BookingRepository {

    private final BookingJpaRepository jpaRepository;
    private final BookingEntityMapper mapper;

    public Booking save(Booking booking) {
        BookingEntity entity = mapper.toEntity(booking);
        BookingEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    public Optional<Booking> findById(BookingId id) {
        return jpaRepository.findById(id.value())
                .map(mapper::toDomain);
    }

    public List<Booking> findByCustomerIdAndTenantId(CustomerId customerId, UUID tenantId) {
        return jpaRepository.findByCustomerIdAndTenantId(customerId.value(), tenantId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    // ✅ ADD THIS: New method for Global Customer Dashboard
    public List<Booking> findByCustomerId(CustomerId customerId) {
        return jpaRepository.findByCustomerId(customerId.value()).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    public List<Booking> findByCompanyIdAndTenantId(CompanyId companyId, UUID tenantId) {
        return jpaRepository.findByCompanyIdAndTenantId(companyId.value(), tenantId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    public List<Booking> findByVehicleIdAndTenantId(VehicleId vehicleId, UUID tenantId) {
        return jpaRepository.findByVehicleIdAndTenantId(vehicleId.value(), tenantId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    public long countByCustomerId(CustomerId customerId) {
        return jpaRepository.countByCustomerId(customerId.value());
    }

    public long countByCustomerIdAndStatus(CustomerId customerId, BookingStatus status) {
        return jpaRepository.countByCustomerIdAndStatus(customerId.value(), status);
    }

    public long countByCustomerIdAndStatusIn(CustomerId customerId, List<BookingStatus> statuses) {
        return jpaRepository.countByCustomerIdAndStatusIn(customerId.value(), statuses);
    }
}