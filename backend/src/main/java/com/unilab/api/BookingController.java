package com.unilab.api;

import com.unilab.dto.AttachEventRequest;
import com.unilab.dto.BookingDto;
import com.unilab.dto.BookingEventDto;
import com.unilab.dto.CancelBookingRequest;
import com.unilab.dto.SlotAvailabilityDto;
import com.unilab.repository.UserRepository;
import com.unilab.security.JwtService;
import com.unilab.service.BookingEventService;
import com.unilab.service.BookingService;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
@Tag(name = "Bookings", description = "Booking management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class BookingController {

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingEventService bookingEventService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    // Get all bookings
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'STUDENT')")
    @Operation(summary = "Get all bookings", description = "Retrieve all bookings (admin only)")
    public ResponseEntity<List<BookingDto>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    // Get booking by ID
    @GetMapping("/{id}")
    @Operation(summary = "Get booking by ID", description = "Retrieve a specific booking")
    public ResponseEntity<BookingDto> getBookingById(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }

    // Get bookings by user
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get bookings by user", description = "Retrieve all bookings for a specific user")
    public ResponseEntity<List<BookingDto>> getBookingsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(bookingService.getBookingsByUserId(userId));
    }

    // Get bookings by date range
    @GetMapping("/filter")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get bookings by date range", description = "Retrieve bookings within a date range")
    public ResponseEntity<List<BookingDto>> getBookingsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        return ResponseEntity.ok(bookingService.getBookingsByDateRange(startDate, endDate));
    }

    // Get bookings by status
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get bookings by status", description = "Retrieve bookings with a specific status")
    public ResponseEntity<List<BookingDto>> getBookingsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(bookingService.getBookingsByStatus(status));
    }

    // Create booking
    @PostMapping
    public ResponseEntity<List<BookingDto>> createBooking(@Valid @RequestBody BookingDto dto, Authentication authentication) {

        String email = (String) authentication.getPrincipal();

        Long userId = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();

        return ResponseEntity.ok(bookingService.createBooking(dto, userId));
    }

    // Attach event to booking
    @PostMapping("/{id}/events")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Attach an event to a booking", description = "Link an existing event to a booking")
    public ResponseEntity<BookingEventDto> attachEventToBooking(
            @PathVariable("id") Long bookingId,
            @Valid @RequestBody AttachEventRequest request,
            Authentication authentication
    ) {
        BookingEventDto dto = bookingEventService.attachEventToBooking(bookingId, request, authentication.getName());
        return ResponseEntity.ok(dto);
    }

    // Get all booking-event links
    @GetMapping("/events")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Get all booking-event links", description = "Retrieve all event associations for bookings")
    public ResponseEntity<List<BookingEventDto>> getAllBookingEvents() {
        return ResponseEntity.ok(bookingEventService.getAllBookingEvents());
    }

    // Detach event
    @DeleteMapping("/{id}/events")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Detach event from booking", description = "Remove linked event from a booking")
    public ResponseEntity<Void> detachEventFromBooking(@PathVariable("id") Long bookingId) {
        bookingEventService.detachEventFromBooking(bookingId);
        return ResponseEntity.noContent().build();
    }


    // Cancel booking
    @PutMapping("/cancel")
    @Operation(summary = "Cancel booking", description = "Cancel a booking with optional refund")
    public ResponseEntity<BookingDto> cancelBooking(
            @Valid @RequestBody CancelBookingRequest request,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User user
    ) {
        String email = user.getUsername();
        Long userId = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();

        return ResponseEntity.ok(bookingService.cancelBooking(request, userId));
    }

    // Approve booking (Admin)
    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Approve booking (Admin only)")
    public ResponseEntity<BookingDto> approveBooking(
            @PathVariable("id") Long id,
            Authentication authentication
    ) {
        String adminEmail = authentication.getName();
        return ResponseEntity.ok(bookingService.approveBooking(id, adminEmail));
    }

    // Reject booking (Admin)
    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Reject booking (Admin only)")
    public ResponseEntity<BookingDto> rejectBooking(
            @PathVariable("id") Long id,
            @RequestParam("reason") String reason,
            Authentication authentication
    ) {
        String adminEmail = authentication.getName();
        return ResponseEntity.ok(bookingService.rejectBooking(id, reason, adminEmail));
    }

    // Pending approvals
    @GetMapping("/pending-approvals")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get pending approvals", description = "Get all bookings pending approval")
    public ResponseEntity<List<BookingDto>> getPendingApprovals() {
        return ResponseEntity.ok(bookingService.getPendingApprovals());
    }

    // Get all booking history by student
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Get all booking history by student", description = "Get all booking history by student")
    public ResponseEntity<List<BookingDto>> getBookingsByStudentId(
            @Parameter(name = "studentId", description = "ID of the student", required = true)
            @PathVariable Long studentId) {
        return ResponseEntity.ok(bookingService.getBookingsByStudentId(studentId));
    }

    // Get all booking history by student
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('STUDENT')")
    @Operation(summary = "Get all student booking history", description = "Get all student booking history")
    public ResponseEntity<List<BookingDto>> getSelfStudentBookings() {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        String token = header.substring(7);
        Claims claims = jwtService.validateAndParse(token);
        var studentId = String.valueOf(claims.getOrDefault("nameIdentifier", "USER"));
        return ResponseEntity.ok(bookingService.getBookingsByStudentId(Long.valueOf(studentId)));
    }
    
    // Get available slots for a lab on a specific date
    @GetMapping("/slots/available")
    @Operation(summary = "Get available slots", description = "Get available booking slots for a lab on a specific date")
    public ResponseEntity<List<SlotAvailabilityDto>> getAvailableSlots(
            @RequestParam("labId") Long labId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(bookingService.getAvailableSlots(labId, date));
    }
}
