package com.unilab.api;

import com.unilab.dto.BookingLimitRequest;
import com.unilab.dto.BookingLimitResponse;
import com.unilab.dto.UpdateBookingLimitRequest;
import com.unilab.service.BookingLimitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/booking-limits")
@Tag(name = "Booking Limits")
public class BookingLimitController {
    
    @Autowired
    private BookingLimitService bookingLimitService;
    
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Create booking limit", description = "Create a new booking limit configuration. Only admins can create booking limits.")
    public ResponseEntity<?> createBookingLimit(@Valid @RequestBody BookingLimitRequest request) {
        try {
            BookingLimitResponse createdLimit = bookingLimitService.createBookingLimit(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdLimit);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Internal server error"));
        }
    }
    
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_LECTURER') or hasAuthority('ROLE_USER')")
    @Operation(summary = "Get all booking limits", description = "Get all booking limit configurations")
    public ResponseEntity<List<BookingLimitResponse>> getAllBookingLimits() {
        try {
            List<BookingLimitResponse> limits = bookingLimitService.getAllBookingLimits();
            return ResponseEntity.ok(limits);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/active")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_LECTURER') or hasAuthority('ROLE_USER')")
    @Operation(summary = "Get active booking limits", description = "Get all active booking limit configurations")
    public ResponseEntity<List<BookingLimitResponse>> getActiveBookingLimits() {
        try {
            List<BookingLimitResponse> limits = bookingLimitService.getActiveBookingLimits();
            return ResponseEntity.ok(limits);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_LECTURER') or hasAuthority('ROLE_USER')")
    @Operation(summary = "Get booking limit by ID", description = "Get a specific booking limit by its ID")
    public ResponseEntity<?> getBookingLimitById(@PathVariable Long id) {
        try {
            BookingLimitResponse limit = bookingLimitService.getBookingLimitById(id);
            return ResponseEntity.ok(limit);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Internal server error"));
        }
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update booking limit", description = "Update an existing booking limit. Only admins can update booking limits.")
    public ResponseEntity<?> updateBookingLimit(@PathVariable("id") Long id, @Valid @RequestBody UpdateBookingLimitRequest request) {
        try {
            BookingLimitResponse updatedLimit = bookingLimitService.updateBookingLimit(id, request);
            return ResponseEntity.ok(updatedLimit);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Internal server error"));
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete booking limit", description = "Delete a booking limit. Only admins can delete booking limits.")
    public ResponseEntity<?> deleteBookingLimit(@PathVariable("id") Long id) {
        try {
            bookingLimitService.deleteBookingLimit(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Internal server error"));
        }
    }
    
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(
        summary = "Update booking limit status",
        description = "Activate (true) or deactivate (false) a booking limit",
        parameters = {
            @Parameter(name = "id", description = "Booking limit ID to update status", required = true, example = "1", in = ParameterIn.PATH),
            @Parameter(name = "active", description = "Set to true to activate, false to deactivate", required = true, example = "true", in = ParameterIn.QUERY)
        }
    )
    public ResponseEntity<?> updateBookingLimitStatus(
            @Parameter(description = "Booking limit ID to update status", required = true, example = "1")
            @PathVariable("id") Long id,
            @Parameter(description = "Set to true to activate, false to deactivate", required = true, example = "true")
            @RequestParam("active") Boolean active) {
        try {
            BookingLimitResponse limit = bookingLimitService.updateBookingLimitStatus(id, active);
            return ResponseEntity.ok(limit);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Internal server error"));
        }
    }
}
