package com.unilab.api;

import com.unilab.dto.BookingRuleDto;
import com.unilab.dto.EventCategoryDto;
import com.unilab.service.ConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/config")
@Tag(name = "Configuration", description = "Global configuration endpoints for event categories and booking rules")
@SecurityRequirement(name = "bearerAuth")
public class ConfigController {
    
    @Autowired
    private ConfigService configService;
    
    // ===== Event Category Endpoints =====
    
    @GetMapping("/categories")
    @Operation(summary = "Get all event categories", description = "Retrieve all event categories")
    public ResponseEntity<List<EventCategoryDto>> getAllEventCategories() {
        List<EventCategoryDto> categories = configService.getAllEventCategories();
        return ResponseEntity.ok(categories);
    }
    
    @GetMapping("/categories/active")
    @Operation(summary = "Get active event categories", description = "Retrieve all active event categories")
    public ResponseEntity<List<EventCategoryDto>> getActiveEventCategories() {
        List<EventCategoryDto> categories = configService.getActiveEventCategories();
        return ResponseEntity.ok(categories);
    }
    
    @GetMapping("/categories/{id}")
    @Operation(summary = "Get event category by ID", description = "Retrieve a specific event category")
    public ResponseEntity<EventCategoryDto> getEventCategoryById(@PathVariable Long id) {
        EventCategoryDto category = configService.getEventCategoryById(id);
        return ResponseEntity.ok(category);
    }
    
    @PostMapping("/categories")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create event category", description = "Create a new event category")
    public ResponseEntity<EventCategoryDto> createEventCategory(@Valid @RequestBody EventCategoryDto dto) {
        EventCategoryDto created = configService.createEventCategory(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @PutMapping("/categories/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update event category", description = "Update an existing event category")
    public ResponseEntity<EventCategoryDto> updateEventCategory(
        @PathVariable Long id,
        @Valid @RequestBody EventCategoryDto dto
    ) {
        EventCategoryDto updated = configService.updateEventCategory(id, dto);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/categories/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete event category", description = "Delete an event category")
    public ResponseEntity<Void> deleteEventCategory(@PathVariable Long id) {
        configService.deleteEventCategory(id);
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/categories/{id}/toggle")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Toggle event category status", description = "Toggle active/inactive status of an event category")
    public ResponseEntity<EventCategoryDto> toggleEventCategoryStatus(@PathVariable Long id) {
        EventCategoryDto category = configService.toggleEventCategoryStatus(id);
        return ResponseEntity.ok(category);
    }
    
    // ===== Booking Rule Endpoints =====
    
    @GetMapping("/rules")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all booking rules", description = "Retrieve all booking rules")
    public ResponseEntity<List<BookingRuleDto>> getAllBookingRules() {
        List<BookingRuleDto> rules = configService.getAllBookingRules();
        return ResponseEntity.ok(rules);
    }
    
    @GetMapping("/rules/active")
    @Operation(summary = "Get active booking rules", description = "Retrieve all active booking rules")
    public ResponseEntity<List<BookingRuleDto>> getActiveBookingRules() {
        List<BookingRuleDto> rules = configService.getActiveBookingRules();
        return ResponseEntity.ok(rules);
    }
    
    @GetMapping("/rules/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get booking rule by ID", description = "Retrieve a specific booking rule")
    public ResponseEntity<BookingRuleDto> getBookingRuleById(@PathVariable Long id) {
        BookingRuleDto rule = configService.getBookingRuleById(id);
        return ResponseEntity.ok(rule);
    }
    
    @PostMapping("/rules")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create booking rule", description = "Create a new booking rule")
    public ResponseEntity<BookingRuleDto> createBookingRule(@Valid @RequestBody BookingRuleDto dto) {
        BookingRuleDto created = configService.createBookingRule(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @PutMapping("/rules/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update booking rule", description = "Update an existing booking rule")
    public ResponseEntity<BookingRuleDto> updateBookingRule(
        @PathVariable Long id,
        @Valid @RequestBody BookingRuleDto dto
    ) {
        BookingRuleDto updated = configService.updateBookingRule(id, dto);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/rules/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete booking rule", description = "Delete a booking rule")
    public ResponseEntity<Void> deleteBookingRule(@PathVariable Long id) {
        configService.deleteBookingRule(id);
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/rules/{id}/toggle")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Toggle booking rule status", description = "Toggle active/inactive status of a booking rule")
    public ResponseEntity<BookingRuleDto> toggleBookingRuleStatus(@PathVariable Long id) {
        BookingRuleDto rule = configService.toggleBookingRuleStatus(id);
        return ResponseEntity.ok(rule);
    }
    
    @GetMapping("/rules/applicable/{userType}")
    @Operation(summary = "Get applicable rules for user type", description = "Get booking rules applicable to a specific user type")
    public ResponseEntity<List<BookingRuleDto>> getApplicableRules(@PathVariable String userType) {
        List<BookingRuleDto> rules = configService.getApplicableRules(userType);
        return ResponseEntity.ok(rules);
    }
}
