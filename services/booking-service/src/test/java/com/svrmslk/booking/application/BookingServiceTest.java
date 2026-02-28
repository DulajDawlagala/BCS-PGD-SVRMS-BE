//package com.svrmslk.booking.application;
//
//import com.svrmslk.booking.application.command.CreateBookingCommand;
//import com.svrmslk.booking.domain.Booking;
//import com.svrmslk.booking.domain.BookingStatus;
//import com.svrmslk.booking.infrastructure.persistence.BookingRepository;
//import com.svrmslk.booking.shared.event.EventPublisher;
//import com.svrmslk.booking.shared.exception.ValidationException;
//import com.svrmslk.booking.shared.security.BookingSecurityContext;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.math.BigDecimal;
//import java.time.Instant;
//import java.time.temporal.ChronoUnit;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class BookingServiceTest {
//
//    @Mock
//    private BookingRepository bookingRepository;
//
//    @Mock
//    private BookingSecurityContext securityContext;
//
//    @Mock
//    private EventPublisher eventPublisher;
//
//    private BookingService bookingService;
//
//    @BeforeEach
//    void setUp() {
//        bookingService = new BookingService(bookingRepository, securityContext, eventPublisher);
//        when(securityContext.getTenantId()).thenReturn(UUID.randomUUID());
//        when(securityContext.getUserId()).thenReturn(UUID.randomUUID());
//    }
//
//    @Test
//    void createBooking_shouldCreateBooking_withValidData() {
//        CreateBookingCommand command = CreateBookingCommand.builder()
//                .vehicleId(UUID.randomUUID())
//                .customerId(UUID.randomUUID())
//                .companyId(UUID.randomUUID())
//                .startTime(Instant.now().plus(1, ChronoUnit.DAYS))
//                .endTime(Instant.now().plus(2, ChronoUnit.DAYS))
//                .price(new BigDecimal("100.00"))
//                .build();
//
//        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArguments()[0]);
//
//        Booking result = bookingService.createBooking(command);
//
//        assertNotNull(result);
//        assertEquals(BookingStatus.PENDING, result.getStatus());
//        verify(bookingRepository).save(any(Booking.class));
//        verify(eventPublisher).publish(eq("booking.created"), any());
//    }
//
//    @Test
//    void createBooking_shouldThrowException_whenStartTimeIsAfterEndTime() {
//        CreateBookingCommand command = CreateBookingCommand.builder()
//                .vehicleId(UUID.randomUUID())
//                .customerId(UUID.randomUUID())
//                .companyId(UUID.randomUUID())
//                .startTime(Instant.now().plus(2, ChronoUnit.DAYS))
//                .endTime(Instant.now().plus(1, ChronoUnit.DAYS))
//                .price(new BigDecimal("100.00"))
//                .build();
//
//        assertThrows(ValidationException.class, () -> bookingService.createBooking(command));
//    }
//
//    @Test
//    void createBooking_shouldThrowException_whenStartTimeIsInPast() {
//        CreateBookingCommand command = CreateBookingCommand.builder()
//                .vehicleId(UUID.randomUUID())
//                .customerId(UUID.randomUUID())
//                .companyId(UUID.randomUUID())
//                .startTime(Instant.now().minus(1, ChronoUnit.DAYS))
//                .endTime(Instant.now().plus(1, ChronoUnit.DAYS))
//                .price(new BigDecimal("100.00"))
//                .build();
//
//        assertThrows(ValidationException.class, () -> bookingService.createBooking(command));
//    }
//}

package com.svrmslk.booking.application;

import com.svrmslk.booking.application.command.CreateBookingCommand;
import com.svrmslk.booking.domain.Booking;
import com.svrmslk.booking.domain.BookingStatus;
import com.svrmslk.booking.infrastructure.persistence.BookingRepository;
import com.svrmslk.booking.shared.event.EventPublisher;
import com.svrmslk.booking.shared.exception.ValidationException;
import com.svrmslk.booking.shared.security.BookingSecurityContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingSecurityContext securityContext;

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private RestTemplate restTemplate; // ✅ NEW

    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        bookingService = new BookingService(
                bookingRepository,
                securityContext,
                eventPublisher,
                restTemplate // ✅ pass it
        );

        when(securityContext.getUserId()).thenReturn(UUID.randomUUID());
        when(securityContext.getUserIdString()).thenReturn(UUID.randomUUID().toString());
        when(securityContext.getEmail()).thenReturn("test@example.com");
        when(securityContext.getRoles()).thenReturn("CUSTOMER");
        when(securityContext.hasTenant()).thenReturn(false);

        // ✅ Mock vehicle status update HTTP call
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.PATCH),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(ResponseEntity.ok("OK"));
    }

    @Test
    void createBooking_shouldCreateBooking_withValidData() {
        CreateBookingCommand command = CreateBookingCommand.builder()
                .vehicleId(UUID.randomUUID())
                .customerId(UUID.randomUUID())
                .companyId(UUID.randomUUID())
                .startTime(Instant.now().plus(1, ChronoUnit.DAYS))
                .endTime(Instant.now().plus(2, ChronoUnit.DAYS))
                .price(new BigDecimal("100.00"))
                .build();

        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Booking result = bookingService.createBooking(command);

        assertNotNull(result);
        assertEquals(BookingStatus.PENDING, result.getStatus());

        verify(bookingRepository).save(any(Booking.class));
        verify(eventPublisher).publish(eq("booking.created"), any());
        verify(restTemplate).exchange(
                anyString(),
                eq(HttpMethod.PATCH),
                any(HttpEntity.class),
                eq(String.class)
        );
    }

    @Test
    void createBooking_shouldThrowException_whenStartTimeIsAfterEndTime() {
        CreateBookingCommand command = CreateBookingCommand.builder()
                .vehicleId(UUID.randomUUID())
                .customerId(UUID.randomUUID())
                .companyId(UUID.randomUUID())
                .startTime(Instant.now().plus(2, ChronoUnit.DAYS))
                .endTime(Instant.now().plus(1, ChronoUnit.DAYS))
                .price(new BigDecimal("100.00"))
                .build();

        assertThrows(ValidationException.class,
                () -> bookingService.createBooking(command));
    }

    @Test
    void createBooking_shouldThrowException_whenStartTimeIsInPast() {
        CreateBookingCommand command = CreateBookingCommand.builder()
                .vehicleId(UUID.randomUUID())
                .customerId(UUID.randomUUID())
                .companyId(UUID.randomUUID())
                .startTime(Instant.now().minus(1, ChronoUnit.DAYS))
                .endTime(Instant.now().plus(1, ChronoUnit.DAYS))
                .price(new BigDecimal("100.00"))
                .build();

        assertThrows(ValidationException.class,
                () -> bookingService.createBooking(command));
    }
}