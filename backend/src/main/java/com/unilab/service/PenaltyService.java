package com.unilab.service;

import com.unilab.dto.PenaltyHistoryDto;
import com.unilab.dto.PenaltyRuleDto;
import com.unilab.model.*;
import com.unilab.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PenaltyService {
    
    @Autowired
    private PenaltyHistoryRepository penaltyHistoryRepository;
    
    @Autowired
    private PenaltyRuleRepository penaltyRuleRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BookingRepository bookingRepository;
    
    // Get all penalty rules
    public List<PenaltyRuleDto> getAllPenaltyRules() {
        return penaltyRuleRepository.findAll().stream()
            .map(this::convertRuleToDto)
            .collect(Collectors.toList());
    }
    
    // Get active penalty rules
    public List<PenaltyRuleDto> getActivePenaltyRules() {
        return penaltyRuleRepository.findAllActive().stream()
            .map(this::convertRuleToDto)
            .collect(Collectors.toList());
    }
    
    // Create penalty rule
    @Transactional
    public PenaltyRuleDto createPenaltyRule(PenaltyRuleDto dto) {
        PenaltyRule rule = new PenaltyRule();
        rule.setCode(dto.getCode());
        rule.setName(dto.getName());
        rule.setDescription(dto.getDescription());
        rule.setViolationType(dto.getViolationType());
        rule.setPenaltyAmount(new BigDecimal(dto.getPenaltyAmount()));
        rule.setPenaltyType(dto.getPenaltyType());
        rule.setPenaltyPoints(dto.getPenaltyPoints());
        rule.setSuspensionDays(dto.getSuspensionDays());
        rule.setIsActive(dto.getIsActive());
        rule.setGracePeriodHours(dto.getGracePeriodHours());
        
        PenaltyRule saved = penaltyRuleRepository.save(rule);
        return convertRuleToDto(saved);
    }
    
    // Update penalty rule
    @Transactional
    public PenaltyRuleDto updatePenaltyRule(Long id, PenaltyRuleDto dto) {
        PenaltyRule rule = penaltyRuleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Penalty rule not found"));
        
        rule.setName(dto.getName());
        rule.setDescription(dto.getDescription());
        rule.setViolationType(dto.getViolationType());
        rule.setPenaltyAmount(new BigDecimal(dto.getPenaltyAmount()));
        rule.setPenaltyType(dto.getPenaltyType());
        rule.setPenaltyPoints(dto.getPenaltyPoints());
        rule.setSuspensionDays(dto.getSuspensionDays());
        rule.setIsActive(dto.getIsActive());
        rule.setGracePeriodHours(dto.getGracePeriodHours());
        
        PenaltyRule saved = penaltyRuleRepository.save(rule);
        return convertRuleToDto(saved);
    }
    
    // Delete penalty rule
    @Transactional
    public void deletePenaltyRule(Long id) {
        penaltyRuleRepository.deleteById(id);
    }
    
    // Get penalty history
    public List<PenaltyHistoryDto> getAllPenaltyHistory() {
        return penaltyHistoryRepository.findAll().stream()
            .map(this::convertHistoryToDto)
            .collect(Collectors.toList());
    }
    
    // Get penalty history by user
    public List<PenaltyHistoryDto> getPenaltyHistoryByUserId(Long userId) {
        return penaltyHistoryRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
            .map(this::convertHistoryToDto)
            .collect(Collectors.toList());
    }
    
    // Get penalty history by date range
    public List<PenaltyHistoryDto> getPenaltyHistoryByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return penaltyHistoryRepository.findByDateRange(startDate, endDate).stream()
            .map(this::convertHistoryToDto)
            .collect(Collectors.toList());
    }
    
    // Apply penalty
    @Transactional
    public PenaltyHistoryDto applyPenalty(Long userId, Long bookingId, String violationType, String reason) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Booking booking = null;
        if (bookingId != null) {
            booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        }
        
        PenaltyRule rule = penaltyRuleRepository.findActiveRuleByViolationType(violationType)
            .orElseThrow(() -> new RuntimeException("No active penalty rule found for violation type: " + violationType));
        
        PenaltyHistory history = new PenaltyHistory();
        history.setUser(user);
        history.setBooking(booking);
        history.setPenaltyRule(rule);
        history.setAmount(rule.getPenaltyAmount());
        history.setPenaltyPoints(rule.getPenaltyPoints());
        history.setStatus("PENDING");
        history.setReason(reason);
        
        PenaltyHistory saved = penaltyHistoryRepository.save(history);
        return convertHistoryToDto(saved);
    }
    
    // Apply penalty by admin
    @Transactional
    public PenaltyHistoryDto applyPenaltyByAdmin(Long userId, Long bookingId, Long ruleId, String reason, Long adminUserId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Booking booking = null;
        if (bookingId != null) {
            booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        }
        
        PenaltyRule rule = penaltyRuleRepository.findById(ruleId)
            .orElseThrow(() -> new RuntimeException("Penalty rule not found"));
        
        PenaltyHistory history = new PenaltyHistory();
        history.setUser(user);
        history.setBooking(booking);
        history.setPenaltyRule(rule);
        history.setAmount(rule.getPenaltyAmount());
        history.setPenaltyPoints(rule.getPenaltyPoints());
        history.setStatus("PENDING");
        history.setReason(reason);
        history.setAppliedByUserId(adminUserId);
        
        PenaltyHistory saved = penaltyHistoryRepository.save(history);
        return convertHistoryToDto(saved);
    }
    
    // Waive penalty
    @Transactional
    public PenaltyHistoryDto waivePenalty(Long penaltyId, Long adminUserId, String notes) {
        PenaltyHistory history = penaltyHistoryRepository.findById(penaltyId)
            .orElseThrow(() -> new RuntimeException("Penalty not found"));
        
        history.setStatus("WAIVED");
        history.setWaivedByUserId(adminUserId);
        history.setWaivedAt(LocalDateTime.now());
        history.setNotes(notes);
        
        PenaltyHistory saved = penaltyHistoryRepository.save(history);
        return convertHistoryToDto(saved);
    }
    
    // Mark penalty as paid
    @Transactional
    public PenaltyHistoryDto markPenaltyAsPaid(Long penaltyId) {
        PenaltyHistory history = penaltyHistoryRepository.findById(penaltyId)
            .orElseThrow(() -> new RuntimeException("Penalty not found"));
        
        history.setStatus("PAID");
        history.setPaidAt(LocalDateTime.now());
        
        PenaltyHistory saved = penaltyHistoryRepository.save(history);
        return convertHistoryToDto(saved);
    }
    
    // Get user total penalty points
    public Integer getUserTotalPenaltyPoints(Long userId) {
        Integer points = penaltyHistoryRepository.getTotalPenaltyPointsByUserId(userId);
        return points != null ? points : 0;
    }
    
    // Helper methods
    private PenaltyRuleDto convertRuleToDto(PenaltyRule rule) {
        PenaltyRuleDto dto = new PenaltyRuleDto();
        dto.setId(rule.getId());
        dto.setCode(rule.getCode());
        dto.setName(rule.getName());
        dto.setDescription(rule.getDescription());
        dto.setViolationType(rule.getViolationType());
        dto.setPenaltyAmount(rule.getPenaltyAmount().toString());
        dto.setPenaltyType(rule.getPenaltyType());
        dto.setPenaltyPoints(rule.getPenaltyPoints());
        dto.setSuspensionDays(rule.getSuspensionDays());
        dto.setIsActive(rule.getIsActive());
        dto.setGracePeriodHours(rule.getGracePeriodHours());
        dto.setCreatedAt(rule.getCreatedAt());
        dto.setUpdatedAt(rule.getUpdatedAt());
        return dto;
    }
    
    private PenaltyHistoryDto convertHistoryToDto(PenaltyHistory history) {
        PenaltyHistoryDto dto = new PenaltyHistoryDto();
        dto.setId(history.getId());
        dto.setUserId(history.getUser().getId());
        dto.setUserEmail(history.getUser().getEmail());
        
        if (history.getBooking() != null) {
            dto.setBookingId(history.getBooking().getId());
            dto.setBookingCode(history.getBooking().getBookingCode());
        }
        
        dto.setPenaltyRuleId(history.getPenaltyRule().getId());
        dto.setPenaltyRuleName(history.getPenaltyRule().getName());
        dto.setViolationType(history.getPenaltyRule().getViolationType());
        dto.setAmount(history.getAmount().toString());
        dto.setPenaltyPoints(history.getPenaltyPoints());
        dto.setStatus(history.getStatus());
        dto.setReason(history.getReason());
        dto.setNotes(history.getNotes());
        dto.setAppliedByUserId(history.getAppliedByUserId());
        dto.setWaivedByUserId(history.getWaivedByUserId());
        dto.setPaidAt(history.getPaidAt());
        dto.setWaivedAt(history.getWaivedAt());
        dto.setCreatedAt(history.getCreatedAt());
        dto.setUpdatedAt(history.getUpdatedAt());
        return dto;
    }
}
