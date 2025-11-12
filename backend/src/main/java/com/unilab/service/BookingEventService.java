package com.unilab.service;

import com.unilab.dto.AttachEventRequest;
import com.unilab.dto.BookingEventDto;
import com.unilab.model.Booking;
import com.unilab.model.BookingEvent;
import com.unilab.model.Event;
import com.unilab.model.User;
import com.unilab.repository.BookingEventRepository;
import com.unilab.repository.BookingRepository;
import com.unilab.repository.EventRepository;
import com.unilab.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BookingEventService {

    private final BookingEventRepository bookingEventRepository;
    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Autowired
    public BookingEventService(BookingEventRepository bookingEventRepository,
                               BookingRepository bookingRepository,
                               EventRepository eventRepository,
                               UserRepository userRepository) {
        this.bookingEventRepository = bookingEventRepository;
        this.bookingRepository = bookingRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public BookingEventDto attachEventToBooking(Long bookingId, AttachEventRequest request, String actorEmail) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with id: " + bookingId));

        if (!"APPROVED".equalsIgnoreCase(booking.getStatus())) {
            throw new IllegalStateException("Booking must be approved before attaching an event");
        }

        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new IllegalArgumentException("Event not found with id: " + request.getEventId()));

        if (!booking.getLab().getId().equals(event.getLab().getId())) {
            throw new IllegalArgumentException("Event lab does not match booking lab");
        }

        Optional<BookingEvent> existingLink = bookingEventRepository.findByBooking_Id(bookingId);

        BookingEvent bookingEvent = existingLink.orElseGet(BookingEvent::new);
        bookingEvent.setBooking(booking);
        bookingEvent.setEvent(event);

        bookingEvent.setStatus("LINKED");
        bookingEvent.setRejectionReason(null);

        bookingEvent.setApprovedByUserId(null);
        bookingEvent.setApprovedAt(null);

        BookingEvent saved = bookingEventRepository.save(bookingEvent);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public BookingEventDto getBookingEvent(Long bookingId) {
        return bookingEventRepository.findByBooking_Id(bookingId)
                .map(this::toDto)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<BookingEventDto> getAllBookingEvents() {
        return bookingEventRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public void detachEventFromBooking(Long bookingId) {
        bookingEventRepository.findByBooking_Id(bookingId)
                .ifPresent(bookingEventRepository::delete);
    }

    private BookingEventDto toDto(BookingEvent bookingEvent) {
        BookingEventDto dto = new BookingEventDto();
        dto.setId(bookingEvent.getId());
        dto.setBookingId(bookingEvent.getBooking().getId());
        dto.setBookingCode(bookingEvent.getBooking().getBookingCode());
        dto.setEventId(bookingEvent.getEvent().getId());
        dto.setEventTitle(bookingEvent.getEvent().getTitle());
        dto.setStatus(bookingEvent.getStatus());
        dto.setApprovedByUserId(bookingEvent.getApprovedByUserId());
        if (bookingEvent.getApprovedByUserId() != null) {
            userRepository.findById(bookingEvent.getApprovedByUserId())
                    .ifPresent(user -> dto.setApprovedByUserName(user.getFullName()));
        }
        dto.setApprovedAt(bookingEvent.getApprovedAt());
        dto.setRejectionReason(bookingEvent.getRejectionReason());
        dto.setCreatedAt(bookingEvent.getCreatedAt());
        dto.setUpdatedAt(bookingEvent.getUpdatedAt());
        return dto;
    }
}

