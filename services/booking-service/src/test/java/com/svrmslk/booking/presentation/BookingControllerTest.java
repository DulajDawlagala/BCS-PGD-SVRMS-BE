package com.svrmslk.booking.presentation;

import com.svrmslk.booking.application.BookingService;
import com.svrmslk.booking.domain.Booking;
import com.svrmslk.booking.domain.BookingStatus;
import com.svrmslk.booking.presentation.mapper.BookingDtoMapper;
import com.svrmslk.booking.shared.domain.BookingId;
import com.svrmslk.booking.shared.domain.CompanyId;
import com.svrmslk.booking.shared.domain.CustomerId;
import com.svrmslk.booking.shared.domain.VehicleId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @MockBean
    private BookingDtoMapper mapper;

    @Test
    void getBooking_shouldReturnBooking_whenBookingExists() throws Exception {
        UUID bookingId = UUID.randomUUID();
        Booking booking = Booking.builder()
                .id(new BookingId(bookingId))
                .vehicleId(VehicleId.of(UUID.randomUUID()))
                .customerId(CustomerId.of(UUID.randomUUID()))
                .companyId(CompanyId.of(UUID.randomUUID()))
                .status(BookingStatus.CONFIRMED)
                .price(new BigDecimal("100.00"))
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        when(bookingService.getBooking(bookingId)).thenReturn(booking);

        mockMvc.perform(get("/api/v1/bookings/{bookingId}", bookingId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}