package com.unilab.api;

import com.unilab.dto.CreateEventRequest;
import com.unilab.dto.EventResponse;
import com.unilab.dto.BookingDto;
import com.unilab.repository.UserRepository;
import com.unilab.service.EventService;
import com.unilab.service.BookingEventService;
import com.unilab.service.EventParticipantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@Tag(name = "Events", description = "Event management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private BookingEventService bookingEventService;

    @Autowired
    private EventParticipantService eventParticipantService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    @Operation(summary = "List events", description = "Retrieve all events")
    public ResponseEntity<List<EventResponse>> list() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get event by ID")
    public ResponseEntity<EventResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Create an event", description = "Create a new event for a lab (Admin and Teacher only)")
    public ResponseEntity<EventResponse> create(
            @Valid @RequestBody CreateEventRequest request,
            Authentication authentication
    ) {
        String email = authentication.getName();
        Long userId = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();

        EventResponse created = eventService.createEvent(request, userId);
        return ResponseEntity.ok(created);
    }

    // Get available events (for teacher to select when booking)
    @GetMapping("/available")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @Operation(summary = "Get available events", description = "Retrieve all APPROVED events that teachers can select when booking")
    public ResponseEntity<List<EventResponse>> getAvailableEvents() {
        List<EventResponse> allEvents = eventService.getAllEvents();
        // Filter only APPROVED events
        List<EventResponse> approvedEvents = allEvents.stream()
                .filter(e -> "APPROVED".equalsIgnoreCase(e.getStatus()))
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(approvedEvents);
    }

    // Get participants (bookings) for an event (for admin and teacher)
    @GetMapping("/{eventId}/participants")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Get event participants", description = "Get all bookings (by teachers) that are linked to this event")
    public ResponseEntity<List<BookingDto>> getEventParticipants(
            @PathVariable Long eventId
    ) {
        List<BookingDto> participants = bookingEventService.getBookingsByEventId(eventId);
        return ResponseEntity.ok(participants);
    }

    // Get pending events (Admin only)
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get pending events", description = "Get all events pending approval (Admin only)")
    public ResponseEntity<List<EventResponse>> getPendingEvents() {
        return ResponseEntity.ok(eventService.getPendingEvents());
    }

    // Approve event (Admin only)
    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Approve event (Admin only)", description = "Approve a pending event")
    public ResponseEntity<EventResponse> approveEvent(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String adminEmail = authentication.getName();
        return ResponseEntity.ok(eventService.approveEvent(id, adminEmail));
    }

    // Reject event (Admin only)
    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Reject event (Admin only)", description = "Reject a pending event")
    public ResponseEntity<EventResponse> rejectEvent(
            @PathVariable Long id,
            @RequestParam(value = "reason", required = false) String reason,
            Authentication authentication
    ) {
        String adminEmail = authentication.getName();
        return ResponseEntity.ok(eventService.rejectEvent(id, reason != null ? reason : "Rejected by admin", adminEmail));
    }

    // Join event (Student only)
    @PostMapping("/{eventId}/join")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Join event (Student only)", description = "Student joins an APPROVED event")
    public ResponseEntity<?> joinEvent(
            @PathVariable Long eventId,
            Authentication authentication
    ) {
        String email = authentication.getName();
        Long userId = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();

        eventParticipantService.joinEvent(eventId, userId);
        return ResponseEntity.ok().body(java.util.Map.of("message", "Successfully joined event"));
    }

    // Leave event (Student only)
    @PostMapping("/{eventId}/leave")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Leave event (Student only)", description = "Student leaves an event they joined")
    public ResponseEntity<?> leaveEvent(
            @PathVariable Long eventId,
            Authentication authentication
    ) {
        String email = authentication.getName();
        Long userId = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();

        eventParticipantService.leaveEvent(eventId, userId);
        return ResponseEntity.ok().body(java.util.Map.of("message", "Successfully left event"));
    }

    // Get events that student has joined
    @GetMapping("/my-events")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get my events (Student only)", description = "Get all events that the student has joined")
    public ResponseEntity<List<EventResponse>> getMyEvents(Authentication authentication) {
        String email = authentication.getName();
        Long userId = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();

        List<EventResponse> events = eventParticipantService.getParticipatedEvents(userId).stream()
                .map(ep -> eventService.getEventById(ep.getEvent().getId()))
                .collect(java.util.stream.Collectors.toList());

        return ResponseEntity.ok(events);
    }
}
