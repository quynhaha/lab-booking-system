package com.unilab.service;

import com.unilab.dto.*;
import com.unilab.model.*;
import com.unilab.repository.*;
import com.unilab.util.TimeSlotUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookingService {
    
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private LabRepository labRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EventCategoryRepository eventCategoryRepository;
    
    @Autowired
    private BookingRuleRepository bookingRuleRepository;
    
    @Autowired
    private PenaltyService penaltyService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private BookingEventService bookingEventService;

    @Autowired
    private BookingEventRepository bookingEventRepository;

    @Autowired
    private EventRepository eventRepository;

    // Get all bookings
    public List<BookingDto> getAllBookings() {
        return bookingRepository.findAll().stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    // Get booking by ID
    public BookingDto getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));
        return convertToDto(booking);
    }
    
    // Get bookings by user
    public List<BookingDto> getBookingsByUserId(Long userId) {
        return bookingRepository.findByUserId(userId).stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    // Get bookings by date range
    public List<BookingDto> getBookingsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return bookingRepository.findByDateRange(startDate, endDate).stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    // Get bookings by status
    public List<BookingDto> getBookingsByStatus(String status) {
        return bookingRepository.findByStatus(status).stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    //Create booking
    @Transactional
    public List<BookingDto> createBooking(BookingDto dto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Validate: Only TEACHER can select event when booking, STUDENT cannot
        if (dto.getEventId() != null) {
            String userRole = user.getRole() != null ? user.getRole().getName() : "";
            if (!"TEACHER".equalsIgnoreCase(userRole) && !"ADMIN".equalsIgnoreCase(userRole)) {
                throw new IllegalStateException("Only TEACHER can book with event. STUDENT cannot select event when booking.");
            }
        }

        List<BookingDto> results = new ArrayList<>();
        String parentCode = generateBookingCode();
        Long parentId = null;

        String initialStatus = resolveInitialStatus(dto.getStatus());

        for (int i = 0; i < dto.getLabSlots().size(); i++) {
            LabBookingSlotDto slot = dto.getLabSlots().get(i);
            Lab lab = labRepository.findById(slot.getLabId())
                    .orElseThrow(() -> new RuntimeException("Lab not found: " + slot.getLabId()));

            // Validate slot number and booking date
            if (slot.getSlotNumber() == null) {
                throw new RuntimeException("slotNumber is required (must be 1, 2, 3, or 4)");
            }
            
            if (slot.getBookingDate() == null || slot.getBookingDate().isEmpty()) {
                throw new RuntimeException("bookingDate is required. Format: YYYY-MM-DD");
            }
            
            // Set startTime and endTime automatically based on slot number
            LocalDateTime startTime;
            LocalDateTime endTime;
            
            try {
                LocalDateTime bookingDate = java.time.LocalDate.parse(slot.getBookingDate()).atStartOfDay();
                startTime = TimeSlotUtil.getSlotStartTime(slot.getSlotNumber(), bookingDate);
                endTime = TimeSlotUtil.getSlotEndTime(slot.getSlotNumber(), bookingDate);
            } catch (Exception e) {
                throw new RuntimeException("Invalid bookingDate format. Expected format: YYYY-MM-DD", e);
            }
            
            // Check conflicts
            checkConflicts(slot.getLabId(), startTime, endTime);

            Booking booking = new Booking();
            booking.setBookingCode(parentCode + "-" + (i + 1));
            booking.setUser(user);
            booking.setLab(lab);
            booking.setTitle(slot.getTitle());
            booking.setDescription(slot.getDescription());
            booking.setParticipantsCount(slot.getParticipantsCount());
            booking.setStartTime(startTime);
            booking.setEndTime(endTime);
            booking.setIsMultiLab(true);
            booking.setStatus(initialStatus);

            if (i > 0 && parentId != null) booking.setParentBookingId(parentId);

            Booking saved = bookingRepository.save(booking);
            if (i == 0) parentId = saved.getId();

            results.add(convertToDto(saved));

            // Link event if provided
            if (dto.getEventId() != null) {
                AttachEventRequest attachRequest = new AttachEventRequest();
                attachRequest.setEventId(dto.getEventId());
                bookingEventService.attachEventToBooking(saved.getId(), attachRequest, user.getEmail());
            }
        }

        return results;
    }
    
    /**
     * Validate that booking times match a valid slot
     */
    private void validateSlotBooking(LocalDateTime startTime, LocalDateTime endTime) {
        if (!TimeSlotUtil.isValidSlotBooking(startTime, endTime)) {
            throw new RuntimeException("Booking times must match one of the predefined slots. " +
                    "Available slots: Ca 1 (08:00-11:00), Ca 2 (11:00-14:00), Ca 3 (14:00-17:00), Ca 4 (17:00-20:00)");
        }
    }

    private void checkConflicts(Long labId, LocalDateTime start, LocalDateTime end) {
        // Check for conflicting bookings
        List<Booking> conflictingBookings = bookingRepository.findConflictingBookings(labId, start, end);
        if (!conflictingBookings.isEmpty()) {
            Booking conflict = conflictingBookings.get(0);
            String conflictInfo = String.format(
                "Phòng lab đã được đặt trong khoảng thời gian này. " +
                "Booking hiện tại: '%s' từ %s đến %s (Trạng thái: %s)",
                conflict.getTitle() != null ? conflict.getTitle() : "Không có tiêu đề",
                conflict.getStartTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                conflict.getEndTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                conflict.getStatus()
            );
            throw new IllegalStateException(conflictInfo);
        }

        // Check for conflicting events (only APPROVED events with lab and times)
        java.time.OffsetDateTime startOffset = start.atOffset(java.time.ZoneOffset.UTC);
        java.time.OffsetDateTime endOffset = end.atOffset(java.time.ZoneOffset.UTC);
        
        List<com.unilab.model.Event> conflictingEvents = eventRepository.findConflictingEvents(
            labId, startOffset, endOffset);
        
        // Filter only APPROVED events that have lab and times assigned
        conflictingEvents = conflictingEvents.stream()
            .filter(e -> "APPROVED".equalsIgnoreCase(e.getStatus()))
            .filter(e -> e.getLab() != null)
            .filter(e -> e.getStartTime() != null && e.getEndTime() != null)
            .collect(java.util.stream.Collectors.toList());
        
        if (!conflictingEvents.isEmpty()) {
            com.unilab.model.Event conflict = conflictingEvents.get(0);
            String conflictInfo = String.format(
                "Phòng lab đã được đặt cho sự kiện trong khoảng thời gian này. " +
                "Sự kiện: '%s' từ %s đến %s",
                conflict.getTitle() != null ? conflict.getTitle() : "Không có tiêu đề",
                conflict.getStartTime().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                conflict.getEndTime().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
            );
            throw new IllegalStateException(conflictInfo);
        }
    }

    /**
     * Resolve initial status for new booking
     * Defaults to PENDING if not specified
     */
    private String resolveInitialStatus(String status) {
        if (status != null && !status.trim().isEmpty()) {
            return status;
        }
        return "PENDING"; // Default status - requires admin approval
    }

    private Booking createBookingEntity(BookingDto dto, User user, Lab lab, String code) {
        Booking booking = new Booking();
        booking.setBookingCode(code);
        booking.setUser(user);
        booking.setLab(lab);
        booking.setTitle(dto.getTitle());
        booking.setDescription(dto.getDescription());
        booking.setStartTime(dto.getStartTime());
        booking.setEndTime(dto.getEndTime());
        booking.setStatus(resolveInitialStatus(dto.getStatus()));
        booking.setParticipantsCount(dto.getParticipantsCount());
        booking.setIsMultiLab(dto.getIsMultiLab());
        if (dto.getCategoryId() != null) {
            EventCategory category = eventCategoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            booking.setCategory(category);
        }
        return booking;
    }


    // Cancel booking with refund
    @Transactional
    public BookingDto cancelBooking(CancelBookingRequest request, Long userId) {
        Booking booking = bookingRepository.findById(request.getBookingId())
            .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        if (!booking.getStatus().equals("PENDING") && !booking.getStatus().equals("APPROVED")) {
            throw new RuntimeException("Cannot cancel booking with status: " + booking.getStatus());
        }
        
        booking.setStatus("CANCELLED");
        booking.setCancellationReason(request.getCancellationReason());
        booking.setCancelledByUserId(userId);
        booking.setCancelledAt(LocalDateTime.now());
        
        // Calculate refund if requested
        if (Boolean.TRUE.equals(request.getWithRefund())) {
            BigDecimal refundAmount = calculateRefund(booking);
            booking.setRefundAmount(refundAmount);
            booking.setRefundStatus("PENDING");
            
            // Check if penalty applies for late cancellation
            applyLateCancellationPenalty(booking);
        }
        
        // If it's a multi-lab booking, cancel all related bookings
        if (Boolean.TRUE.equals(booking.getIsMultiLab())) {
            if (booking.getParentBookingId() == null) {
                // This is the parent, cancel all children
                List<Booking> children = bookingRepository.findChildBookings(booking.getId());
                for (Booking child : children) {
                    child.setStatus("CANCELLED");
                    child.setCancellationReason(request.getCancellationReason());
                    child.setCancelledByUserId(userId);
                    child.setCancelledAt(LocalDateTime.now());
                    bookingRepository.save(child);
                }
            }
        }
        
        Booking saved = bookingRepository.save(booking);
        return convertToDto(saved);
    }
    
    // Approve booking
//    public BookingDto approveBooking(Long bookingId, String adminEmail) {
//        Booking booking = bookingRepository.findById(bookingId)
//                .orElseThrow(() -> new RuntimeException("Booking not found"));
//        booking.getUser().getEmail();
//        User admin = userRepository.findByEmail(adminEmail)
//                .orElseThrow(() -> new RuntimeException("Admin not found"));
//
//        booking.setStatus("APPROVED");
//        booking.setApprovedByUserId(admin.getId());
//        booking.setApprovedAt(LocalDateTime.now());
//        booking.setUpdatedAt(LocalDateTime.now());
//
//        bookingRepository.save(booking);
//
//        try {
//            emailService.sendBookingApprovalEmail(
//                    booking.getUser().getEmail(),
//                    booking.getTitle(),
//                    booking.getStartTime()
//            );
//        } catch (Exception e) {
//            System.err.println("Failed to send approval email: " + e.getMessage());
//        }
//
//        return BookingDto.fromEntity(booking);
//    }

    public BookingDto approveBooking(Long bookingId, String adminEmail) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // 🔍 DEBUG
        System.out.println("===== APPROVE DEBUG START =====");
        System.out.println("Booking ID: " + bookingId);
        System.out.println("Admin Email: " + adminEmail);

        if (booking.getUser() != null) {
            System.out.println("Booking user email: " + booking.getUser().getEmail());
        } else {
            System.out.println("booking.getUser() = NULL (NO EMAIL LOADED)");
        }
        System.out.println("===== APPROVE DEBUG END =====");

        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        booking.setStatus("APPROVED");
        booking.setApprovedByUserId(admin.getId());
        booking.setApprovedAt(LocalDateTime.now());
        booking.setUpdatedAt(LocalDateTime.now());

        bookingRepository.save(booking);

        try {
            emailService.sendBookingApprovalEmail(
                    booking.getUser().getEmail(),   // ⛔ Có thể bị null
                    booking.getTitle(),
                    booking.getStartTime()
            );
        } catch (Exception e) {
            System.err.println("Failed to send approval email: " + e.getMessage());
        }

        return BookingDto.fromEntity(booking);
    }

    //Reject booking
    @Transactional
    public BookingDto rejectBooking(Long bookingId, String reason, String adminEmail) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        // Update booking fields
        booking.setStatus("REJECTED");
        booking.setApprovedByUserId(admin.getId());
        booking.setApprovedAt(LocalDateTime.now());
        booking.setRejectionReason(reason);
        booking.setUpdatedAt(LocalDateTime.now());

        bookingRepository.save(booking);

        // Send rejection email
        try {
            emailService.sendBookingRejectionEmail(
                    booking.getUser().getEmail(),
                    booking.getTitle(),
                    booking.getStartTime(),
                    reason
            );
        } catch (Exception e) {
            System.err.println("Failed to send rejection email: " + e.getMessage());
        }

        return BookingDto.fromEntity(booking);
    }

    // Get pending approvals
    public List<BookingDto> getPendingApprovals() {
        return bookingRepository.findPendingApprovals().stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    // Helper methods
    private String generateBookingCode() {
        return "BK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    private BigDecimal calculateRefund(Booking booking) {
        // Simple refund calculation - can be made more sophisticated
        long hoursUntilStart = java.time.Duration.between(LocalDateTime.now(), booking.getStartTime()).toHours();
        
        if (hoursUntilStart > 48) {
            return new BigDecimal("100.00"); // Full refund
        } else if (hoursUntilStart > 24) {
            return new BigDecimal("50.00"); // 50% refund
        } else {
            return BigDecimal.ZERO; // No refund
        }
    }
    
    private void applyLateCancellationPenalty(Booking booking) {
        long hoursUntilStart = java.time.Duration.between(LocalDateTime.now(), booking.getStartTime()).toHours();
        
        if (hoursUntilStart < 24) {
            // Apply late cancellation penalty
            penaltyService.applyPenalty(booking.getUser().getId(), booking.getId(), 
                "LATE_CANCELLATION", "Cancelled less than 24 hours before event");
        }
    }
    
    private void validateBookingRules(BookingDto bookingDto, User user) {
        // Get user role
        String userType = "STUDENT"; // Default
        if (user.getRole() != null) {
            userType = user.getRole().getName();
        }
        
        List<BookingRule> rules = bookingRuleRepository.findApplicableRules(userType);
        
        for (BookingRule rule : rules) {
            // Apply rule validation logic here
            // This is a simplified version
            if (rule.getRuleType().equals("MAX_DURATION")) {
                // Validate duration
            }
        }
    }
    
    private BookingDto convertToDto(Booking booking) {
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setBookingCode(booking.getBookingCode());
        dto.setUserId(booking.getUser().getId());
        dto.setUserEmail(booking.getUser().getEmail());
        dto.setUserName(booking.getUser().getFullName());
        dto.setLabId(booking.getLab().getId());
        dto.setLabName(booking.getLab().getName());
        // dto.setLabCode(booking.getLab().getLabCode()); // labCode field removed from Lab entity
        
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
        
        // Get child bookings if this is a parent
        if (Boolean.TRUE.equals(booking.getIsMultiLab()) && booking.getParentBookingId() == null) {
            List<Long> childIds = bookingRepository.findChildBookings(booking.getId()).stream()
                .map(Booking::getId)
                .collect(Collectors.toList());
            dto.setChildBookingIds(childIds);
        }
        
        return dto;
    }

    public List<BookingDto> getBookingsByStudentId(Long userId) {
        //check role of user -- student only
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!"STUDENT".equalsIgnoreCase(user.getRole().getName())) {
            throw new AccessDeniedException("Only studentId allowed to get bookings");
        }

        return bookingRepository.findAllByStudentId(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Get available slots for a lab on a specific date
     */
    public List<SlotAvailabilityDto> getAvailableSlots(Long labId, LocalDate date) {
        // Verify lab exists
        Lab lab = labRepository.findById(labId)
                .orElseThrow(() -> new RuntimeException("Lab not found: " + labId));
        
        // Get all bookings for this lab on this date
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);
        List<Booking> bookings = bookingRepository.findByLabIdAndDateRange(labId, startOfDay, endOfDay);
        
        // Get all slot numbers (1-4)
        List<Integer> allSlots = TimeSlotUtil.getAllSlotNumbers();
        List<SlotAvailabilityDto> slotAvailability = new ArrayList<>();
        
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        
        for (Integer slotNumber : allSlots) {
            SlotAvailabilityDto slotDto = new SlotAvailabilityDto();
            slotDto.setSlotNumber(slotNumber);
            slotDto.setSlotName(TimeSlotUtil.getSlotName(slotNumber));
            
            LocalDateTime slotStart = TimeSlotUtil.getSlotStartTime(slotNumber, startOfDay);
            LocalDateTime slotEnd = TimeSlotUtil.getSlotEndTime(slotNumber, startOfDay);
            
            slotDto.setStartTime(slotStart.toLocalTime().format(timeFormatter));
            slotDto.setEndTime(slotEnd.toLocalTime().format(timeFormatter));
            
            // Check if slot is available
            boolean isAvailable = true;
            String reason = null;
            
            for (Booking booking : bookings) {
                // Skip cancelled and rejected bookings
                if ("CANCELLED".equals(booking.getStatus()) || "REJECTED".equals(booking.getStatus())) {
                    continue;
                }
                
                // Check if booking overlaps with this slot
                if (TimeSlotUtil.overlapsWithSlot(booking.getStartTime(), booking.getEndTime(), slotNumber)) {
                    isAvailable = false;
                    reason = "Slot is already booked";
                    break;
                }
            }
            
            slotDto.setIsAvailable(isAvailable);
            slotDto.setReason(reason);
            slotAvailability.add(slotDto);
        }
        
        return slotAvailability;
    }

}