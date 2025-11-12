package com.unilab.service;

import com.unilab.model.Event;
import com.unilab.model.EventParticipant;
import com.unilab.model.User;
import com.unilab.repository.EventParticipantRepository;
import com.unilab.repository.EventRepository;
import com.unilab.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EventParticipantService {

    @Autowired
    private EventParticipantRepository eventParticipantRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public EventParticipant joinEvent(Long eventId, Long userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found with id: " + eventId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        // Check if event is APPROVED
        if (!"APPROVED".equalsIgnoreCase(event.getStatus())) {
            throw new IllegalStateException("Only APPROVED events can be joined");
        }

        // Check if already joined
        Optional<EventParticipant> existing = eventParticipantRepository.findByEvent_IdAndUser_Id(eventId, userId);
        if (existing.isPresent()) {
            EventParticipant participant = existing.get();
            if ("JOINED".equalsIgnoreCase(participant.getStatus())) {
                throw new IllegalStateException("You have already joined this event");
            }
            // Rejoin if previously left
            participant.setStatus("JOINED");
            return eventParticipantRepository.save(participant);
        }

        // Create new participation
        EventParticipant participant = new EventParticipant();
        participant.setEvent(event);
        participant.setUser(user);
        participant.setStatus("JOINED");

        return eventParticipantRepository.save(participant);
    }

    @Transactional
    public void leaveEvent(Long eventId, Long userId) {
        EventParticipant participant = eventParticipantRepository.findByEvent_IdAndUser_Id(eventId, userId)
                .orElseThrow(() -> new IllegalArgumentException("You are not participating in this event"));

        participant.setStatus("LEFT");
        eventParticipantRepository.save(participant);
    }

    @Transactional(readOnly = true)
    public List<EventParticipant> getParticipatedEvents(Long userId) {
        return eventParticipantRepository.findActiveParticipationsByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<EventParticipant> getEventParticipants(Long eventId) {
        return eventParticipantRepository.findByEvent_Id(eventId);
    }

    @Transactional(readOnly = true)
    public boolean isParticipating(Long eventId, Long userId) {
        return eventParticipantRepository.existsByEvent_IdAndUser_Id(eventId, userId) &&
               eventParticipantRepository.findByEvent_IdAndUser_Id(eventId, userId)
                       .map(p -> "JOINED".equalsIgnoreCase(p.getStatus()))
                       .orElse(false);
    }
}

