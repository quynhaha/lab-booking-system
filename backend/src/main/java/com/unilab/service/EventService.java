package com.unilab.service;

import com.unilab.dto.CreateEventRequest;
import com.unilab.dto.EventResponse;
import com.unilab.model.Event;
import com.unilab.model.Lab;
import com.unilab.model.User;
import com.unilab.repository.EventRepository;
import com.unilab.repository.LabRepository;
import com.unilab.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private LabRepository labRepository;

    @Autowired
    private UserRepository userRepository;

    public EventResponse createEvent(CreateEventRequest request, Long userId) {
        // Validate business rules
        validateEventRequest(request);

        // Check if user exists and fetch role
        User user = userRepository.findByIdWithRole(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Determine initial status based on user role
        // ADMIN creates events → automatically APPROVED
        // TEACHER creates events → PENDING (requires admin approval)
        String initialStatus;
        if (user.getRole() != null && "ADMIN".equalsIgnoreCase(user.getRole().getName())) {
            initialStatus = "APPROVED";
        } else {
            initialStatus = "PENDING";
        }

        // Lab is optional - check if lab exists only if labId is provided
        Lab lab = null;
        if (request.getLabId() != null) {
            lab = labRepository.findById(request.getLabId())
                    .orElseThrow(() -> new IllegalArgumentException("Lab not found"));

            // Check for time conflicts only if lab and times are provided
            if (request.getStartTime() != null && request.getEndTime() != null) {
                List<Event> conflictingEvents = eventRepository.findConflictingEvents(
                        request.getLabId(), request.getStartTime(), request.getEndTime());
                
                if (!conflictingEvents.isEmpty()) {
                    throw new IllegalArgumentException("Time conflict: Lab is already booked during this time period");
                }
            }
        }

        // Create new event
        Event event = new Event();
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setStartTime(request.getStartTime()); // Can be null
        event.setEndTime(request.getEndTime()); // Can be null
        event.setUser(user);
        event.setLab(lab); // Can be null
        event.setStatus(initialStatus);
        event.setIsPrivate(false);
        event.setInvitees(null);
        event.setCreatedAt(OffsetDateTime.now());

        Event savedEvent = eventRepository.save(event);

        return convertToEventResponse(savedEvent);
    }

    public List<EventResponse> getEventsByUser(Long userId) {
        List<Event> events = eventRepository.findByUserId(userId);
        return events.stream()
                .map(this::convertToEventResponse)
                .collect(Collectors.toList());
    }

    public List<EventResponse> getAllEvents() {
        List<Event> events = eventRepository.findAll();
        return events.stream()
                .map(this::convertToEventResponse)
                .collect(Collectors.toList());
    }

    public EventResponse getEventById(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));
        return convertToEventResponse(event);
    }

    // Get pending events (for admin approval)
    public List<EventResponse> getPendingEvents() {
        List<Event> events = eventRepository.findAll().stream()
                .filter(e -> "PENDING".equalsIgnoreCase(e.getStatus()))
                .collect(Collectors.toList());
        return events.stream()
                .map(this::convertToEventResponse)
                .collect(Collectors.toList());
    }

    // Approve event (Admin only)
    public EventResponse approveEvent(Long eventId, String adminEmail) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        if (!"PENDING".equalsIgnoreCase(event.getStatus())) {
            throw new IllegalStateException("Only PENDING events can be approved");
        }

        event.setStatus("APPROVED");
        Event savedEvent = eventRepository.save(event);
        return convertToEventResponse(savedEvent);
    }

    // Reject event (Admin only)
    public EventResponse rejectEvent(Long eventId, String reason, String adminEmail) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        if (!"PENDING".equalsIgnoreCase(event.getStatus())) {
            throw new IllegalStateException("Only PENDING events can be rejected");
        }

        event.setStatus("REJECTED");
        Event savedEvent = eventRepository.save(event);
        return convertToEventResponse(savedEvent);
    }

    private void validateEventRequest(CreateEventRequest request) {
        // Validate time logic only if both startTime and endTime are provided
        if (request.getStartTime() != null && request.getEndTime() != null) {
            if (request.getStartTime().isAfter(request.getEndTime())) {
                throw new IllegalArgumentException("Start time must be before end time");
            }

            // Validate that event is not in the past
            if (request.getStartTime().isBefore(OffsetDateTime.now())) {
                throw new IllegalArgumentException("Cannot create events in the past");
            }

            // Validate minimum duration (e.g., 30 minutes)
            if (request.getStartTime().plusMinutes(30).isAfter(request.getEndTime())) {
                throw new IllegalArgumentException("Event duration must be at least 30 minutes");
            }
        }
    }

    public EventResponse duplicateEvent(Long eventId, Long userId) {
        // Find the original event
        Event originalEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));
        
        // Validate that the event is in the past
        if (originalEvent.getStartTime().isAfter(OffsetDateTime.now())) {
            throw new IllegalArgumentException("Cannot duplicate future events");
        }
        
        // Create a new event based on the original
        Event duplicatedEvent = new Event();
        duplicatedEvent.setTitle(originalEvent.getTitle() + " (Copy)");
        duplicatedEvent.setDescription(originalEvent.getDescription());
        duplicatedEvent.setStartTime(originalEvent.getStartTime().plusDays(7)); // Schedule for next week
        duplicatedEvent.setEndTime(originalEvent.getEndTime().plusDays(7));
        duplicatedEvent.setStatus("DRAFT"); // Set as draft for editing
        duplicatedEvent.setUser(userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found")));
        duplicatedEvent.setLab(originalEvent.getLab());
        
        // Save the duplicated event
        Event savedEvent = eventRepository.save(duplicatedEvent);
        return convertToEventResponse(savedEvent);
    }
    
    private EventResponse convertToEventResponse(Event event) {
        return new EventResponse(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getStartTime(),
                event.getEndTime(),
                event.getUser().getId(),
                event.getUser().getFullName(),
                event.getLab() != null ? event.getLab().getId() : null,
                event.getLab() != null ? event.getLab().getName() : null,
                event.getStatus(),
                event.getCreatedAt()
        );
    }
}

