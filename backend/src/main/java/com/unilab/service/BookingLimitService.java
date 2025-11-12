package com.unilab.service;

import com.unilab.dto.BookingLimitRequest;
import com.unilab.dto.BookingLimitResponse;
import com.unilab.dto.UpdateBookingLimitRequest;
import com.unilab.model.BookingLimit;
import com.unilab.repository.BookingLimitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookingLimitService {
    
    @Autowired
    private BookingLimitRepository bookingLimitRepository;
    
    public BookingLimitResponse createBookingLimit(BookingLimitRequest request) {
        // Check if limit with same name already exists
        if (bookingLimitRepository.findByName(request.getName()).isPresent()) {
            throw new IllegalArgumentException("Booking limit with this name already exists");
        }
        
        // Create new booking limit
        BookingLimit bookingLimit = new BookingLimit();
        bookingLimit.setName(request.getName());
        bookingLimit.setMaxBookingsPerUser(request.getMaxBookingsPerUser());
        bookingLimit.setMaxDurationHours(request.getMaxDurationHours());
        bookingLimit.setAdvanceBookingDays(request.getAdvanceBookingDays());
        bookingLimit.setIsActive(true);
        bookingLimit.setCreatedAt(LocalDateTime.now());
        bookingLimit.setUpdatedAt(LocalDateTime.now());
        
        BookingLimit savedLimit = bookingLimitRepository.save(bookingLimit);
        return convertToBookingLimitResponse(savedLimit);
    }
    
    public List<BookingLimitResponse> getAllBookingLimits() {
        List<BookingLimit> limits = bookingLimitRepository.findAll();
        return limits.stream()
                .map(this::convertToBookingLimitResponse)
                .collect(Collectors.toList());
    }
    
    public List<BookingLimitResponse> getActiveBookingLimits() {
        List<BookingLimit> limits = bookingLimitRepository.findByIsActiveTrue();
        return limits.stream()
                .map(this::convertToBookingLimitResponse)
                .collect(Collectors.toList());
    }
    
    public BookingLimitResponse getBookingLimitById(Long id) {
        BookingLimit limit = bookingLimitRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking limit not found"));
        return convertToBookingLimitResponse(limit);
    }
    
    public BookingLimitResponse updateBookingLimit(Long id, UpdateBookingLimitRequest request) {
        BookingLimit limit = bookingLimitRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking limit not found"));
        
        // Check if name is being changed and if new name already exists
        if (!limit.getName().equals(request.getName()) && 
            bookingLimitRepository.findByName(request.getName()).isPresent()) {
            throw new IllegalArgumentException("Booking limit with this name already exists");
        }
        
        limit.setName(request.getName());
        limit.setMaxBookingsPerUser(request.getMaxBookingsPerUser());
        limit.setMaxDurationHours(request.getMaxDurationHours());
        limit.setAdvanceBookingDays(request.getAdvanceBookingDays());
        if (request.getIsActive() != null) {
            limit.setIsActive(request.getIsActive());
        }
        limit.setUpdatedAt(LocalDateTime.now());
        
        BookingLimit savedLimit = bookingLimitRepository.save(limit);
        return convertToBookingLimitResponse(savedLimit);
    }
    
    public void deleteBookingLimit(Long id) {
        BookingLimit limit = bookingLimitRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking limit not found"));
        bookingLimitRepository.delete(limit);
    }
    
    public BookingLimitResponse updateBookingLimitStatus(Long id, Boolean active) {
        BookingLimit limit = bookingLimitRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking limit not found"));
        
        String oldStatus = limit.getIsActive() ? "ACTIVE" : "INACTIVE";
        String newStatus = active ? "ACTIVE" : "INACTIVE";
        limit.setIsActive(active);
        limit.setUpdatedAt(LocalDateTime.now());
        
        BookingLimit savedLimit = bookingLimitRepository.save(limit);
        
        // Log the status change
        System.out.println("Booking Limit ID " + id + " status changed from " + oldStatus + " to " + newStatus);
        
        return convertToBookingLimitResponse(savedLimit);
    }
    
    private BookingLimitResponse convertToBookingLimitResponse(BookingLimit limit) {
        return new BookingLimitResponse(
                limit.getId(),
                limit.getName(),
                limit.getMaxBookingsPerUser(),
                limit.getMaxDurationHours(),
                limit.getAdvanceBookingDays(),
                limit.getIsActive(),
                limit.getCreatedAt(),
                limit.getUpdatedAt()
        );
    }
}
