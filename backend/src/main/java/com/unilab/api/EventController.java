package com.unilab.api;

import com.unilab.dto.CreateEventRequest;
import com.unilab.dto.EventResponse;
import com.unilab.repository.UserRepository;
import com.unilab.service.EventService;
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
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Create an event", description = "Create a new event for a lab")
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
}
