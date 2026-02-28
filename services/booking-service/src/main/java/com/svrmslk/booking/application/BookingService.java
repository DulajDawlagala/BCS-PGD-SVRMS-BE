//package com.svrmslk.booking.application;
//
//import com.svrmslk.booking.application.command.CancelBookingCommand;
//import com.svrmslk.booking.application.command.CreateBookingCommand;
//import com.svrmslk.booking.domain.Booking;
//import com.svrmslk.booking.domain.BookingStatus;
//import com.svrmslk.booking.infrastructure.event.BookingCancelledEvent;
//import com.svrmslk.booking.infrastructure.event.BookingCreatedEvent;
//import com.svrmslk.booking.infrastructure.persistence.BookingRepository;
//import com.svrmslk.booking.shared.domain.BookingId;
//import com.svrmslk.booking.shared.domain.CompanyId;
//import com.svrmslk.booking.shared.domain.CustomerId;
//import com.svrmslk.booking.shared.domain.VehicleId;
//import com.svrmslk.booking.shared.event.EventMetadata;
//import com.svrmslk.booking.shared.event.EventPublisher;
//import com.svrmslk.booking.shared.exception.BookingNotFoundException;
//import com.svrmslk.booking.shared.exception.UnauthorizedException;
//import com.svrmslk.booking.shared.exception.ValidationException;
//import com.svrmslk.booking.shared.security.BookingSecurityContext;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.client.RestTemplate;
//
//import java.time.Instant;
//import java.util.List;
//import java.util.UUID;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class BookingService {
//
//    private final BookingRepository bookingRepository;
//    private final BookingSecurityContext securityContext;
//    private final EventPublisher eventPublisher;
//    private final RestTemplate restTemplate;
//
//    @Value("${services.company-service.url:http://localhost:8086}")
//    private String companyServiceUrl;
//
//    @Transactional
//    public Booking createBooking(CreateBookingCommand command) {
//        validateBookingTimes(command.getStartTime(), command.getEndTime());
//
//        // ✅ FIX 1: Always use company_id as tenant_id for bookings
//        UUID tenantId = command.getCompanyId();
//
//        Booking booking = Booking.builder()
//                .id(BookingId.generate())
//                .vehicleId(VehicleId.of(command.getVehicleId()))
//                .customerId(CustomerId.of(command.getCustomerId()))
//                .companyId(CompanyId.of(command.getCompanyId()))
//                .tenantId(tenantId)  // ✅ ALWAYS equals company_id
//                .startTime(command.getStartTime())
//                .endTime(command.getEndTime())
//                .status(BookingStatus.PENDING)
//                .price(command.getPrice())
//                .createdAt(Instant.now())
//                .updatedAt(Instant.now())
//                .build();
//
//        Booking saved = bookingRepository.save(booking);
//
//        // ✅ FIX 2: Update vehicle status to RENTED
//        updateVehicleStatus(saved.getVehicleId().value(), "RENTED");
//
//        publishBookingCreatedEvent(saved);
//
//        log.info("Booking created: {} with tenant_id: {}", saved.getId(), tenantId);
//        return saved;
//    }
//
//    @Transactional
//    public Booking confirmBooking(UUID bookingId) {
//        Booking booking = bookingRepository.findById(new BookingId(bookingId))
//                .orElseThrow(() -> new BookingNotFoundException(bookingId));
//
//        // Only company admin or user with OWNER role can confirm
//        if (!securityContext.isCompanyAdmin() && !securityContext.hasRole("OWNER")) {
//            throw new UnauthorizedException("Only company/owner can confirm bookings");
//        }
//
//        booking.confirm();
//        Booking updated = bookingRepository.save(booking);
//
//        // ✅ Vehicle already set to RENTED in createBooking, no change needed
//
//        log.info("Booking confirmed: {}", updated.getId());
//        return updated;
//    }
//
//    @Transactional
//    public Booking completeBooking(UUID bookingId) {
//        Booking booking = bookingRepository.findById(new BookingId(bookingId))
//                .orElseThrow(() -> new BookingNotFoundException(bookingId));
//
//        // Only company admin or owner can complete
//        if (!securityContext.isCompanyAdmin() && !securityContext.hasRole("OWNER")) {
//            throw new UnauthorizedException("Only company/owner can complete bookings");
//        }
//
//        if (booking.getStatus() != BookingStatus.CONFIRMED) {
//            throw new ValidationException("Only confirmed bookings can be completed");
//        }
//
//        booking.setStatus(BookingStatus.COMPLETED);
//        booking.setUpdatedAt(Instant.now());
//
//        Booking updated = bookingRepository.save(booking);
//
//        // ✅ FIX 2: Set vehicle back to AVAILABLE
//        updateVehicleStatus(updated.getVehicleId().value(), "AVAILABLE");
//
//        log.info("Booking completed: {}", updated.getId());
//        return updated;
//    }
//
//    @Transactional
//    public Booking cancelBooking(CancelBookingCommand command) {
//        Booking booking = bookingRepository.findById(new BookingId(command.getBookingId()))
//                .orElseThrow(() -> new BookingNotFoundException(command.getBookingId()));
//
//        verifyBookingAccess(booking);
//
//        booking.cancel();
//
//        Booking updated = bookingRepository.save(booking);
//
//        // ✅ FIX 2: Set vehicle back to AVAILABLE when booking cancelled
//        updateVehicleStatus(updated.getVehicleId().value(), "AVAILABLE");
//
//        publishBookingCancelledEvent(updated, command.getReason());
//
//        log.info("Booking cancelled: {}", updated.getId());
//        return updated;
//    }
//
//    public Booking getBooking(UUID bookingId) {
//        Booking booking = bookingRepository.findById(new BookingId(bookingId))
//                .orElseThrow(() -> new BookingNotFoundException(bookingId));
//
//        verifyBookingAccess(booking);
//
//        return booking;
//    }
//
//    public List<Booking> getMyBookings() {
//        UUID customerId = securityContext.getUserId();
//
//        // For customers, tenant can be null
//        UUID tenantId = null;
//        if (!securityContext.isCustomer() && securityContext.hasTenant()) {
//            tenantId = securityContext.getTenantId();
//        }
//
//        return bookingRepository.findByCustomerIdAndTenantId(CustomerId.of(customerId), tenantId);
//    }
//
//    public List<Booking> getBookingsByCompany(UUID companyId) {
//        // Use company_id as tenant filter since tenant_id = company_id
//        return bookingRepository.findByCompanyIdAndTenantId(CompanyId.of(companyId), companyId);
//    }
//
//    // ✅ NEW METHOD: Update vehicle status in company-service via HTTP
//    private void updateVehicleStatus(UUID vehicleId, String status) {
//        try {
//            String url = companyServiceUrl + "/api/v1/vehicles/" + vehicleId + "/status?status=" + status;
//
//            HttpHeaders headers = new HttpHeaders();
//
//            // Add user context headers
//            headers.set("X-USER-ID", securityContext.getUserIdString());
//            headers.set("X-EMAIL", securityContext.getEmail());
//            headers.set("X-GLOBAL-ROLES", securityContext.getRoles());
//
//            // Add tenant if available (for OWNER/COMPANY_ADMIN)
//            if (securityContext.hasTenant()) {
//                headers.set("X-TENANT-ID", securityContext.getTenantIdString());
//            }
//
//            // Add session if available
//            if (securityContext.getSessionId() != null) {
//                headers.set("X-SESSION-ID", securityContext.getSessionId());
//            }
//
//            HttpEntity<Void> entity = new HttpEntity<>(headers);
//
//            restTemplate.exchange(url, HttpMethod.PATCH, entity, String.class);
//
//            log.info("✅ Vehicle status updated: {} -> {}", vehicleId, status);
//
//        } catch (Exception e) {
//            // Log but don't fail the booking transaction
//            // Vehicle status sync is important but not critical for MVP
//            log.error("⚠️ Failed to update vehicle status for {}: {}", vehicleId, e.getMessage());
//        }
//    }
//
//    private void validateBookingTimes(Instant startTime, Instant endTime) {
//        if (startTime.isAfter(endTime)) {
//            throw new ValidationException("Start time must be before end time");
//        }
//        if (startTime.isBefore(Instant.now())) {
//            throw new ValidationException("Start time cannot be in the past");
//        }
//    }
//
//    private void verifyBookingAccess(Booking booking) {
//        UUID userId = securityContext.getUserId();
//        boolean isOwner = booking.getCustomerId().value().equals(userId);
//        boolean isCompanyAdmin = securityContext.isCompanyAdmin();
//
//        if (!isOwner && !isCompanyAdmin) {
//            throw new UnauthorizedException("You don't have access to this booking");
//        }
//    }
//
//    private void publishBookingCreatedEvent(Booking booking) {
//        BookingCreatedEvent event = new BookingCreatedEvent(
//                booking.getId().value(),
//                booking.getVehicleId().value(),
//                booking.getCustomerId().value(),
//                booking.getCompanyId().value(),
//                booking.getTenantId(),
//                booking.getStartTime(),
//                booking.getEndTime(),
//                booking.getPrice(),
//                Instant.now(),
//                buildMetadata(booking.getId().value())
//        );
//        eventPublisher.publish("booking.created", event);
//    }
//
//    private void publishBookingCancelledEvent(Booking booking, String reason) {
//        BookingCancelledEvent event = new BookingCancelledEvent(
//                booking.getId().value(),
//                booking.getVehicleId().value(),
//                booking.getCustomerId().value(),
//                booking.getCompanyId().value(),
//                booking.getTenantId(),
//                reason,
//                Instant.now(),
//                buildMetadata(booking.getId().value())
//        );
//        eventPublisher.publish("booking.cancelled", event);
//    }
//
//    private EventMetadata buildMetadata(UUID aggregateId) {
//        // Use company_id as tenant since tenant_id = company_id for bookings
//        UUID tenantId = null;
//        if (!securityContext.isCustomer() && securityContext.hasTenant()) {
//            tenantId = securityContext.getTenantId();
//        }
//
//        return new EventMetadata(
//                UUID.randomUUID(),
//                "1.0",
//                "booking-service",
//                tenantId,
//                securityContext.getUserId()
//        );
//    }
//}

package com.svrmslk.booking.application;

import com.svrmslk.booking.application.command.CancelBookingCommand;
import com.svrmslk.booking.application.command.CreateBookingCommand;
import com.svrmslk.booking.domain.Booking;
import com.svrmslk.booking.domain.BookingStatus;
import com.svrmslk.booking.infrastructure.event.BookingCancelledEvent;
import com.svrmslk.booking.infrastructure.event.BookingCreatedEvent;
import com.svrmslk.booking.infrastructure.persistence.BookingRepository;
import com.svrmslk.booking.shared.domain.BookingId;
import com.svrmslk.booking.shared.domain.CompanyId;
import com.svrmslk.booking.shared.domain.CustomerId;
import com.svrmslk.booking.shared.domain.VehicleId;
import com.svrmslk.booking.shared.event.EventMetadata;
import com.svrmslk.booking.shared.event.EventPublisher;
import com.svrmslk.booking.shared.exception.BookingNotFoundException;
import com.svrmslk.booking.shared.exception.UnauthorizedException;
import com.svrmslk.booking.shared.exception.ValidationException;
import com.svrmslk.booking.shared.security.BookingSecurityContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.svrmslk.booking.presentation.dto.BookingStatsResponse;


@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingSecurityContext securityContext;
    private final EventPublisher eventPublisher;
    private final RestTemplate restTemplate;

    @Value("${services.company-service.url}")
    private String companyServiceUrl;

    @Transactional
    public Booking createBooking(CreateBookingCommand command) {
        log.info("➡️ createBooking | userId={} roles={} vehicleId={} companyId={}",
                securityContext.getUserId(),
                securityContext.getRoles(),
                command.getVehicleId(),
                command.getCompanyId());

        validateBookingTimes(command.getStartTime(), command.getEndTime());

        UUID tenantId = command.getCompanyId();

        Booking booking = Booking.builder()
                .id(BookingId.generate())
                .vehicleId(VehicleId.of(command.getVehicleId()))
                .customerId(CustomerId.of(command.getCustomerId()))
                .companyId(CompanyId.of(command.getCompanyId()))
                .tenantId(tenantId)
                .startTime(command.getStartTime())
                .endTime(command.getEndTime())
                .status(BookingStatus.PENDING)
                .price(command.getPrice())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        Booking saved = bookingRepository.save(booking);

        log.info("✅ Booking saved | bookingId={} status={}", saved.getId(), saved.getStatus());

        updateVehicleStatus(saved.getVehicleId().value(), "RENTED");

        publishBookingCreatedEvent(saved);

        log.info("🎉 Booking created | bookingId={} tenantId={}", saved.getId(), tenantId);
        return saved;
    }

    @Transactional
    public Booking confirmBooking(UUID bookingId) {
        log.info("➡️ confirmBooking | bookingId={} userId={} roles={}",
                bookingId,
                securityContext.getUserId(),
                securityContext.getRoles());

        Booking booking = bookingRepository.findById(new BookingId(bookingId))
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        if (!securityContext.isCompanyAdmin() && !securityContext.hasRole("OWNER")) {
            log.warn("❌ Unauthorized confirm attempt | bookingId={} userId={}",
                    bookingId, securityContext.getUserId());
            throw new UnauthorizedException("Only company/owner can confirm bookings");
        }

        booking.confirm();
        Booking updated = bookingRepository.save(booking);

        log.info("✅ Booking confirmed | bookingId={}", updated.getId());
        return updated;
    }

    @Transactional
    public Booking completeBooking(UUID bookingId) {
        log.info("➡️ completeBooking | bookingId={} userId={} roles={}",
                bookingId,
                securityContext.getUserId(),
                securityContext.getRoles());

        Booking booking = bookingRepository.findById(new BookingId(bookingId))
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        if (!securityContext.isCompanyAdmin() && !securityContext.hasRole("OWNER")) {
            log.warn("❌ Unauthorized complete attempt | bookingId={} userId={}",
                    bookingId, securityContext.getUserId());
            throw new UnauthorizedException("Only company/owner can complete bookings");
        }

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            log.warn("❌ Invalid booking state | bookingId={} currentStatus={}",
                    bookingId, booking.getStatus());
            throw new ValidationException("Only confirmed bookings can be completed");
        }

        booking.setStatus(BookingStatus.COMPLETED);
        booking.setUpdatedAt(Instant.now());

        Booking updated = bookingRepository.save(booking);

        log.info("✅ Booking completed | bookingId={}", updated.getId());

        updateVehicleStatus(updated.getVehicleId().value(), "AVAILABLE");

        return updated;
    }

    @Transactional
    public Booking cancelBooking(CancelBookingCommand command) {
        log.info("➡️ cancelBooking | bookingId={} userId={} roles={} reason={}",
                command.getBookingId(),
                securityContext.getUserId(),
                securityContext.getRoles(),
                command.getReason());

        Booking booking = bookingRepository.findById(new BookingId(command.getBookingId()))
                .orElseThrow(() -> new BookingNotFoundException(command.getBookingId()));

        verifyBookingAccess(booking);

        booking.cancel();

        Booking updated = bookingRepository.save(booking);

        log.info("✅ Booking cancelled | bookingId={}", updated.getId());

        updateVehicleStatus(updated.getVehicleId().value(), "AVAILABLE");

        publishBookingCancelledEvent(updated, command.getReason());

        return updated;
    }

    public Booking getBooking(UUID bookingId) {
        log.info("➡️ getBooking | bookingId={} userId={} roles={}",
                bookingId,
                securityContext.getUserId(),
                securityContext.getRoles());

        Booking booking = bookingRepository.findById(new BookingId(bookingId))
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        verifyBookingAccess(booking);

        return booking;
    }

//    public List<Booking> getMyBookings() {
//        log.info("➡️ getMyBookings | userId={} roles={}",
//                securityContext.getUserId(),
//                securityContext.getRoles());
//
//        UUID customerId = securityContext.getUserId();
//
//        UUID tenantId = null;
//        if (!securityContext.isCustomer() && securityContext.hasTenant()) {
//            tenantId = securityContext.getTenantId();
//        }
//
//        return bookingRepository.findByCustomerIdAndTenantId(CustomerId.of(customerId), tenantId);
//    }

    public List<Booking> getMyBookings() {
        UUID customerId = securityContext.getUserId();

        log.info("➡️ getMyBookings | userId={} isCustomer={}",
                customerId, securityContext.isCustomer());

        // ✅ FIX: If it's a customer, ignore tenantId entirely to get all their bookings
        if (securityContext.isCustomer()) {
            return bookingRepository.findByCustomerId(CustomerId.of(customerId));
        }

        // For non-customers (Staff/Admins), filter by their specific tenant
        UUID tenantId = securityContext.hasTenant() ? securityContext.getTenantId() : null;
        return bookingRepository.findByCustomerIdAndTenantId(CustomerId.of(customerId), tenantId);
    }

    public List<Booking> getBookingsByCompany(UUID companyId) {
        log.info("➡️ getBookingsByCompany | companyId={} userId={} roles={}",
                companyId,
                securityContext.getUserId(),
                securityContext.getRoles());

        return bookingRepository.findByCompanyIdAndTenantId(CompanyId.of(companyId), companyId);
    }

    // 🔎 ONLY LOGS ADDED HERE
    private void updateVehicleStatus(UUID vehicleId, String status) {
        System.out.println("🚨🚨🚨 UPDATE VEHICLE STATUS CALLED: vehicleId=" + vehicleId + " status=" + status);
        log.info("➡️ updateVehicleStatus | vehicleId={} status={} calledByUser={} roles={}",
                vehicleId,
                status,
                securityContext.getUserId(),
                securityContext.getRoles());

        try {
            String url = companyServiceUrl
                    + "/api/v1/vehicles/" + vehicleId + "/status?status=" + status;

            log.info("➡️ PATCH {}", url);

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", securityContext.getUserIdString());
            headers.set("X-EMAIL", securityContext.getEmail());
            headers.set("X-GLOBAL-ROLES", securityContext.getRoles());

            if (securityContext.hasTenant()) {
                headers.set("X-TENANT-ID", securityContext.getTenantIdString());
            }

            if (securityContext.getSessionId() != null) {
                headers.set("X-SESSION-ID", securityContext.getSessionId());
            }

            log.info("➡️ Headers | USER={} ROLES={} TENANT={}",
                    securityContext.getUserId(),
                    securityContext.getRoles(),
                    securityContext.hasTenant() ? securityContext.getTenantId() : "NONE");

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            System.out.println("🚨 About to call RestTemplate.exchange()");

            restTemplate.exchange(url, HttpMethod.PATCH, entity, String.class);

            log.info("✅ Vehicle status updated | vehicleId={} newStatus={}", vehicleId, status);

        } catch (Exception e) {
            System.out.println("🚨 ERROR: " + e.getMessage());
            log.error("⚠️ Vehicle status update FAILED | vehicleId={} status={} error={}",
                    vehicleId, status, e.getMessage(), e);
        }
    }

    private void validateBookingTimes(Instant startTime, Instant endTime) {
        if (startTime.isAfter(endTime)) {
            log.warn("❌ Invalid booking time | start={} end={}", startTime, endTime);
            throw new ValidationException("Start time must be before end time");
        }
        if (startTime.isBefore(Instant.now())) {
            log.warn("❌ Booking start in past | start={}", startTime);
            throw new ValidationException("Start time cannot be in the past");
        }
    }

    private void verifyBookingAccess(Booking booking) {
        UUID userId = securityContext.getUserId();
        boolean isOwner = booking.getCustomerId().value().equals(userId);
        boolean isCompanyAdmin = securityContext.isCompanyAdmin();

        log.info("🔐 verifyBookingAccess | bookingId={} userId={} isOwner={} isCompanyAdmin={}",
                booking.getId(), userId, isOwner, isCompanyAdmin);

        if (!isOwner && !isCompanyAdmin) {
            log.warn("❌ Access denied | bookingId={} userId={}", booking.getId(), userId);
            throw new UnauthorizedException("You don't have access to this booking");
        }
    }

    private void publishBookingCreatedEvent(Booking booking) {
        log.info("📣 Publishing booking.created | bookingId={}", booking.getId());

        BookingCreatedEvent event = new BookingCreatedEvent(
                booking.getId().value(),
                booking.getVehicleId().value(),
                booking.getCustomerId().value(),
                booking.getCompanyId().value(),
                booking.getTenantId(),
                booking.getStartTime(),
                booking.getEndTime(),
                booking.getPrice(),
                Instant.now(),
                buildMetadata(booking.getId().value())
        );
        eventPublisher.publish("booking.created", event);
    }

    private void publishBookingCancelledEvent(Booking booking, String reason) {
        log.info("📣 Publishing booking.cancelled | bookingId={} reason={}",
                booking.getId(), reason);

        BookingCancelledEvent event = new BookingCancelledEvent(
                booking.getId().value(),
                booking.getVehicleId().value(),
                booking.getCustomerId().value(),
                booking.getCompanyId().value(),
                booking.getTenantId(),
                reason,
                Instant.now(),
                buildMetadata(booking.getId().value())
        );
        eventPublisher.publish("booking.cancelled", event);
    }

    private EventMetadata buildMetadata(UUID aggregateId) {
        UUID tenantId = null;
        if (!securityContext.isCustomer() && securityContext.hasTenant()) {
            tenantId = securityContext.getTenantId();
        }

        log.debug("🧾 EventMetadata | aggregateId={} tenantId={} userId={}",
                aggregateId, tenantId, securityContext.getUserId());

        return new EventMetadata(
                UUID.randomUUID(),
                "1.0",
                "booking-service",
                tenantId,
                securityContext.getUserId()
        );
    }

    public BookingStatsResponse getBookingStats(UUID customerId) {
        log.info("➡️ getBookingStats | customerId={}", customerId);

        long totalBookings = bookingRepository.countByCustomerId(CustomerId.of(customerId));

        long activeBookings = bookingRepository.countByCustomerIdAndStatusIn(
                CustomerId.of(customerId),
                List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED)
        );

        long completedBookings = bookingRepository.countByCustomerIdAndStatus(
                CustomerId.of(customerId),
                BookingStatus.COMPLETED
        );

        long cancelledBookings = bookingRepository.countByCustomerIdAndStatus(
                CustomerId.of(customerId),
                BookingStatus.CANCELLED
        );

        return new BookingStatsResponse(
                (int) totalBookings,
                (int) activeBookings,
                (int) completedBookings,
                (int) cancelledBookings
        );
    }
}