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

        // Check if user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Check if lab exists
        Lab lab = labRepository.findById(request.getLabId())
                .orElseThrow(() -> new IllegalArgumentException("Lab not found"));

        // Check for time conflicts
        List<Event> conflictingEvents = eventRepository.findConflictingEvents(
                request.getLabId(), request.getStartTime(), request.getEndTime());
        
        if (!conflictingEvents.isEmpty()) {
            throw new IllegalArgumentException("Time conflict: Lab is already booked during this time period");
        }

        // Create new event
        Event event = new Event();
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setStartTime(request.getStartTime());
        event.setEndTime(request.getEndTime());
        event.setUser(user);
        event.setLab(lab);
        event.setStatus("PENDING");
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

    private void validateEventRequest(CreateEventRequest request) {
        // Validate time logic
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
                event.getLab().getId(),
                event.getLab().getName(),
                event.getStatus(),
                event.getCreatedAt()
        );
    }
}

