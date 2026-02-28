////BookingController.java
//package com.svrmslk.booking.presentation;
//
//import com.svrmslk.booking.application.BookingService;
//import com.svrmslk.booking.application.command.CancelBookingCommand;
//import com.svrmslk.booking.application.command.CreateBookingCommand;
//import com.svrmslk.booking.domain.Booking;
//import com.svrmslk.booking.presentation.dto.BookingResponse;
//import com.svrmslk.booking.presentation.dto.CancelBookingRequest;
//import com.svrmslk.booking.presentation.dto.CreateBookingRequest;
//import com.svrmslk.booking.presentation.mapper.BookingDtoMapper;
//import com.svrmslk.booking.shared.api.ApiResponse;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.UUID;
//import java.util.stream.Collectors;
//
//@RestController
//@RequestMapping("/api/v1/bookings")
//@RequiredArgsConstructor
//@Tag(name = "Booking Management")
//public class BookingController {
//
//    private final BookingService bookingService;
//    private final BookingDtoMapper mapper;
//
//    @PostMapping
//    @Operation(summary = "Create new booking")
//    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(@RequestBody CreateBookingRequest request) {
//        CreateBookingCommand command = mapper.toCreateCommand(request);
//        Booking booking = bookingService.createBooking(command);
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(ApiResponse.success(mapper.toResponse(booking), "Booking created successfully"));
//    }
//
//    @GetMapping("/me")
//    @Operation(summary = "Get my bookings")
//    public ResponseEntity<ApiResponse<List<BookingResponse>>> getMyBookings() {
//        List<Booking> bookings = bookingService.getMyBookings();
//        List<BookingResponse> responses = bookings.stream()
//                .map(mapper::toResponse)
//                .collect(Collectors.toList());
//        return ResponseEntity.ok(ApiResponse.success(responses));
//    }
//
//    @GetMapping("/{bookingId}")
//    @Operation(summary = "Get booking by ID")
//    public ResponseEntity<ApiResponse<BookingResponse>> getBooking(@PathVariable UUID bookingId) {
//        Booking booking = bookingService.getBooking(bookingId);
//        return ResponseEntity.ok(ApiResponse.success(mapper.toResponse(booking)));
//    }
//
//    @PutMapping("/{bookingId}/cancel")
//    @Operation(summary = "Cancel booking")
//    public ResponseEntity<ApiResponse<BookingResponse>> cancelBooking(
//            @PathVariable UUID bookingId,
//            @RequestBody(required = false) CancelBookingRequest request) {
//
//        CancelBookingCommand command = CancelBookingCommand.builder()
//                .bookingId(bookingId)
//                .reason(request != null ? request.getReason() : "Cancelled by user")
//                .build();
//
//        Booking booking = bookingService.cancelBooking(command);
//        return ResponseEntity.ok(ApiResponse.success(mapper.toResponse(booking), "Booking cancelled successfully"));
//    }
//}

package com.svrmslk.booking.presentation;

import com.svrmslk.booking.application.BookingService;
import com.svrmslk.booking.application.command.CancelBookingCommand;
import com.svrmslk.booking.application.command.CreateBookingCommand;
import com.svrmslk.booking.domain.Booking;
import com.svrmslk.booking.presentation.dto.BookingResponse;
import com.svrmslk.booking.presentation.dto.CancelBookingRequest;
import com.svrmslk.booking.presentation.dto.CreateBookingRequest;
import com.svrmslk.booking.presentation.mapper.BookingDtoMapper;
import com.svrmslk.booking.shared.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import com.svrmslk.booking.domain.BookingStatus;
import com.svrmslk.booking.presentation.dto.BookingStatsResponse;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
@Tag(name = "Booking Management")
public class BookingController {

    private final BookingService bookingService;
    private final BookingDtoMapper mapper;

    @PostMapping
    @Operation(summary = "Create new booking")
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(@RequestBody CreateBookingRequest request) {
        CreateBookingCommand command = mapper.toCreateCommand(request);
        Booking booking = bookingService.createBooking(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(mapper.toResponse(booking), "Booking created successfully"));
    }

    @GetMapping("/me")
    @Operation(summary = "Get my bookings")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getMyBookings() {
        List<Booking> bookings = bookingService.getMyBookings();
        List<BookingResponse> responses = bookings.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/{bookingId}")
    @Operation(summary = "Get booking by ID")
    public ResponseEntity<ApiResponse<BookingResponse>> getBooking(@PathVariable UUID bookingId) {
        Booking booking = bookingService.getBooking(bookingId);
        return ResponseEntity.ok(ApiResponse.success(mapper.toResponse(booking)));
    }

    // ✅ NEW: Get bookings for a specific company (owner/admin view)
    @GetMapping("/company/{companyId}")
    @Operation(summary = "Get all bookings for company (owner/admin)")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getCompanyBookings(@PathVariable UUID companyId) {
        List<Booking> bookings = bookingService.getBookingsByCompany(companyId);
        List<BookingResponse> responses = bookings.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    // ✅ NEW: Confirm booking (company/owner action)
    @PutMapping("/{bookingId}/confirm")
    @Operation(summary = "Confirm booking (company/owner)")
    public ResponseEntity<ApiResponse<BookingResponse>> confirmBooking(@PathVariable UUID bookingId) {
        Booking booking = bookingService.confirmBooking(bookingId);
        return ResponseEntity.ok(ApiResponse.success(mapper.toResponse(booking), "Booking confirmed"));
    }

    // ✅ NEW: Complete booking (company/owner or automatic)
    @PutMapping("/{bookingId}/complete")
    @Operation(summary = "Mark booking as completed")
    public ResponseEntity<ApiResponse<BookingResponse>> completeBooking(@PathVariable UUID bookingId) {
        Booking booking = bookingService.completeBooking(bookingId);
        return ResponseEntity.ok(ApiResponse.success(mapper.toResponse(booking), "Booking completed"));
    }

    @PutMapping("/{bookingId}/cancel")
    @Operation(summary = "Cancel booking")
    public ResponseEntity<ApiResponse<BookingResponse>> cancelBooking(
            @PathVariable UUID bookingId,
            @RequestBody(required = false) CancelBookingRequest request) {

        CancelBookingCommand command = CancelBookingCommand.builder()
                .bookingId(bookingId)
                .reason(request != null ? request.getReason() : "Cancelled by user")
                .build();

        Booking booking = bookingService.cancelBooking(command);
        return ResponseEntity.ok(ApiResponse.success(mapper.toResponse(booking), "Booking cancelled successfully"));
    }

    // ✅ NEW: Get booking stats for customer (for customer-service dashboard)
    @GetMapping("/customer/{customerId}/stats")
    @Operation(summary = "Get booking statistics for customer")
    public ResponseEntity<ApiResponse<BookingStatsResponse>> getBookingStats(@PathVariable String customerId) {
        BookingStatsResponse stats = bookingService.getBookingStats(UUID.fromString(customerId));
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

}