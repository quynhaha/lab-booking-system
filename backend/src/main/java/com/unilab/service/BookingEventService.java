package com.unilab.service;

import com.unilab.dto.AttachEventRequest;
import com.unilab.dto.BookingEventDto;
import com.unilab.dto.BookingDto;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new IllegalArgumentException("Event not found with id: " + request.getEventId()));

        if (!"APPROVED".equalsIgnoreCase(event.getStatus())) {
            throw new IllegalStateException("Only APPROVED events can be attached to bookings");
        }

        // If event doesn't have a lab yet, assign it from the booking
        // Also set startTime and endTime from booking if event doesn't have them
        if (event.getLab() == null) {
            event.setLab(booking.getLab());
            // Set event times from booking if event doesn't have times
            if (event.getStartTime() == null) {
                event.setStartTime(booking.getStartTime().atOffset(java.time.ZoneOffset.UTC));
            }
            if (event.getEndTime() == null) {
                event.setEndTime(booking.getEndTime().atOffset(java.time.ZoneOffset.UTC));
            }
            eventRepository.save(event);
        } else {
            // If event already has a lab, check if it matches booking lab
            if (!booking.getLab().getId().equals(event.getLab().getId())) {
                throw new IllegalArgumentException("Event lab does not match booking lab");
            }
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

    @Transactional(readOnly = true)
    public List<BookingDto> getBookingsByEventId(Long eventId) {
        List<BookingEvent> bookingEvents = bookingEventRepository.findByEventId(eventId);
        return bookingEvents.stream()
                .map(be -> convertBookingToDto(be.getBooking()))
                .collect(Collectors.toList());
    }

    private BookingDto convertBookingToDto(Booking booking) {
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setBookingCode(booking.getBookingCode());
        dto.setUserId(booking.getUser().getId());
        dto.setUserEmail(booking.getUser().getEmail());
        dto.setUserName(booking.getUser().getFullName());
        dto.setLabId(booking.getLab().getId());
        dto.setLabName(booking.getLab().getName());
        
        if (booking.getCategory() != null) {
            dto.setCategoryId(booking.getCategory().getId());
            dto.setCategoryName(booking.getCategory().getName());
        }
        
        dto.setTitle(booking.getTitle());
        dto.setDescription(booking.getDescription());
        dto.setStartTime(booking.getStartTime());
        dto.setEndTime(booking.getEndTime());
        dto.setStatus(booking.getStatus());
        dto.setParticipantsCount(booking.getParticipantsCount());
        dto.setIsMultiLab(booking.getIsMultiLab());
        dto.setParentBookingId(booking.getParentBookingId());
        dto.setRefundStatus(booking.getRefundStatus());
        
        if (booking.getRefundAmount() != null) {
            dto.setRefundAmount(booking.getRefundAmount().toString());
        }
        
        dto.setCancellationReason(booking.getCancellationReason());
        dto.setCancelledByUserId(booking.getCancelledByUserId());
        dto.setCancelledAt(booking.getCancelledAt());
        dto.setApprovedByUserId(booking.getApprovedByUserId());
        dto.setApprovedAt(booking.getApprovedAt());
        dto.setCreatedAt(booking.getCreatedAt());
        dto.setUpdatedAt(booking.getUpdatedAt());
        
        return dto;
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