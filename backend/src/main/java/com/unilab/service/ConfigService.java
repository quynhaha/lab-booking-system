package com.unilab.service;

import com.unilab.dto.BookingRuleDto;
import com.unilab.dto.EventCategoryDto;
import com.unilab.model.BookingRule;
import com.unilab.model.EventCategory;
import com.unilab.repository.BookingRuleRepository;
import com.unilab.repository.EventCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConfigService {
    
    @Autowired
    private EventCategoryRepository eventCategoryRepository;
    
    @Autowired
    private BookingRuleRepository bookingRuleRepository;
    
    // ===== Event Category Management =====
    
    // Get all event categories
    public List<EventCategoryDto> getAllEventCategories() {
        return eventCategoryRepository.findAll().stream()
            .map(this::convertCategoryToDto)
            .collect(Collectors.toList());
    }
    
    // Get active event categories
    public List<EventCategoryDto> getActiveEventCategories() {
        return eventCategoryRepository.findAllActive().stream()
            .map(this::convertCategoryToDto)
            .collect(Collectors.toList());
    }
    
    // Get event category by ID
    public EventCategoryDto getEventCategoryById(Long id) {
        EventCategory category = eventCategoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Event category not found with id: " + id));
        return convertCategoryToDto(category);
    }
    
    // Create event category
    @Transactional
    public EventCategoryDto createEventCategory(EventCategoryDto dto) {
        // Check if code already exists
        if (eventCategoryRepository.findByCode(dto.getCode()).isPresent()) {
            throw new RuntimeException("Event category with code " + dto.getCode() + " already exists");
        }
        
        EventCategory category = new EventCategory();
        category.setCode(dto.getCode());
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setColor(dto.getColor());
        category.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        category.setMaxDurationHours(dto.getMaxDurationHours());
        category.setRequiresApproval(dto.getRequiresApproval() != null ? dto.getRequiresApproval() : false);
        
        EventCategory saved = eventCategoryRepository.save(category);
        return convertCategoryToDto(saved);
    }
    
    // Update event category
    @Transactional
    public EventCategoryDto updateEventCategory(Long id, EventCategoryDto dto) {
        EventCategory category = eventCategoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Event category not found with id: " + id));
        
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setColor(dto.getColor());
        category.setIsActive(dto.getIsActive());
        category.setMaxDurationHours(dto.getMaxDurationHours());
        category.setRequiresApproval(dto.getRequiresApproval());
        
        EventCategory saved = eventCategoryRepository.save(category);
        return convertCategoryToDto(saved);
    }
    
    // Delete event category
    @Transactional
    public void deleteEventCategory(Long id) {
        eventCategoryRepository.deleteById(id);
    }
    
    // Toggle event category active status
    @Transactional
    public EventCategoryDto toggleEventCategoryStatus(Long id) {
        EventCategory category = eventCategoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Event category not found with id: " + id));
        
        category.setIsActive(!category.getIsActive());
        EventCategory saved = eventCategoryRepository.save(category);
        return convertCategoryToDto(saved);
    }
    
    // ===== Booking Rule Management =====
    
    // Get all booking rules
    public List<BookingRuleDto> getAllBookingRules() {
        return bookingRuleRepository.findAll().stream()
            .map(this::convertRuleToDto)
            .collect(Collectors.toList());
    }
    
    // Get active booking rules
    public List<BookingRuleDto> getActiveBookingRules() {
        return bookingRuleRepository.findAllActiveOrderedByPriority().stream()
            .map(this::convertRuleToDto)
            .collect(Collectors.toList());
    }
    
    // Get booking rule by ID
    public BookingRuleDto getBookingRuleById(Long id) {
        BookingRule rule = bookingRuleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Booking rule not found with id: " + id));
        return convertRuleToDto(rule);
    }
    
    // Create booking rule
    @Transactional
    public BookingRuleDto createBookingRule(BookingRuleDto dto) {
        // Check if code already exists
        if (bookingRuleRepository.findByCode(dto.getCode()).isPresent()) {
            throw new RuntimeException("Booking rule with code " + dto.getCode() + " already exists");
        }
        
        BookingRule rule = new BookingRule();
        rule.setCode(dto.getCode());
        rule.setName(dto.getName());
        rule.setDescription(dto.getDescription());
        rule.setRuleType(dto.getRuleType());
        rule.setRuleValue(dto.getRuleValue());
        rule.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        rule.setPriority(dto.getPriority());
        rule.setAppliesTo(dto.getAppliesTo());
        rule.setCategoryId(dto.getCategoryId());
        
        BookingRule saved = bookingRuleRepository.save(rule);
        return convertRuleToDto(saved);
    }
    
    // Update booking rule
    @Transactional
    public BookingRuleDto updateBookingRule(Long id, BookingRuleDto dto) {
        BookingRule rule = bookingRuleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Booking rule not found with id: " + id));
        
        rule.setName(dto.getName());
        rule.setDescription(dto.getDescription());
        rule.setRuleType(dto.getRuleType());
        rule.setRuleValue(dto.getRuleValue());
        rule.setIsActive(dto.getIsActive());
        rule.setPriority(dto.getPriority());
        rule.setAppliesTo(dto.getAppliesTo());
        rule.setCategoryId(dto.getCategoryId());
        
        BookingRule saved = bookingRuleRepository.save(rule);
        return convertRuleToDto(saved);
    }
    
    // Delete booking rule
    @Transactional
    public void deleteBookingRule(Long id) {
        bookingRuleRepository.deleteById(id);
    }
    
    // Toggle booking rule active status
    @Transactional
    public BookingRuleDto toggleBookingRuleStatus(Long id) {
        BookingRule rule = bookingRuleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Booking rule not found with id: " + id));
        
        rule.setIsActive(!rule.getIsActive());
        BookingRule saved = bookingRuleRepository.save(rule);
        return convertRuleToDto(saved);
    }
    
    // Get applicable rules for a user
    public List<BookingRuleDto> getApplicableRules(String userType) {
        return bookingRuleRepository.findApplicableRules(userType).stream()
            .map(this::convertRuleToDto)
            .collect(Collectors.toList());
    }
    
    // ===== Helper Methods =====
    
    private EventCategoryDto convertCategoryToDto(EventCategory category) {
        EventCategoryDto dto = new EventCategoryDto();
        dto.setId(category.getId());
        dto.setCode(category.getCode());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setColor(category.getColor());
        dto.setIsActive(category.getIsActive());
        dto.setMaxDurationHours(category.getMaxDurationHours());
        dto.setRequiresApproval(category.getRequiresApproval());
        dto.setCreatedAt(category.getCreatedAt());
        dto.setUpdatedAt(category.getUpdatedAt());
        return dto;
    }
    
    private BookingRuleDto convertRuleToDto(BookingRule rule) {
        BookingRuleDto dto = new BookingRuleDto();
        dto.setId(rule.getId());
        dto.setCode(rule.getCode());
        dto.setName(rule.getName());
        dto.setDescription(rule.getDescription());
        dto.setRuleType(rule.getRuleType());
        dto.setRuleValue(rule.getRuleValue());
        dto.setIsActive(rule.getIsActive());
        dto.setPriority(rule.getPriority());
        dto.setAppliesTo(rule.getAppliesTo());
        dto.setCategoryId(rule.getCategoryId());
        
        if (rule.getCategoryId() != null) {
            eventCategoryRepository.findById(rule.getCategoryId()).ifPresent(category -> {
                dto.setCategoryName(category.getName());
            });
        }
        
        dto.setCreatedAt(rule.getCreatedAt());
        dto.setUpdatedAt(rule.getUpdatedAt());
        return dto;
    }
}
