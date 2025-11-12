package com.unilab.api;

import com.unilab.dto.PenaltyHistoryDto;
import com.unilab.dto.PenaltyRuleDto;
import com.unilab.service.PenaltyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/penalties")
@Tag(name = "Penalty Management", description = "Endpoints for managing penalty rules and history")
@SecurityRequirement(name = "bearerAuth")
public class PenaltyController {
    
    @Autowired
    private PenaltyService penaltyService;
    
    // ===== Penalty Rules Management =====
    
    @GetMapping("/rules")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all penalty rules", description = "Retrieve all penalty rules")
    public ResponseEntity<List<PenaltyRuleDto>> getAllPenaltyRules() {
        List<PenaltyRuleDto> rules = penaltyService.getAllPenaltyRules();
        return ResponseEntity.ok(rules);
    }
    
    @GetMapping("/rules/active")
    @Operation(summary = "Get active penalty rules", description = "Retrieve all active penalty rules")
    public ResponseEntity<List<PenaltyRuleDto>> getActivePenaltyRules() {
        List<PenaltyRuleDto> rules = penaltyService.getActivePenaltyRules();
        return ResponseEntity.ok(rules);
    }
    
    @PostMapping("/rules")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create penalty rule", description = "Create a new penalty rule")
    public ResponseEntity<PenaltyRuleDto> createPenaltyRule(@Valid @RequestBody PenaltyRuleDto dto) {
        PenaltyRuleDto created = penaltyService.createPenaltyRule(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @PutMapping("/rules/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update penalty rule", description = "Update an existing penalty rule")
    public ResponseEntity<PenaltyRuleDto> updatePenaltyRule(
        @PathVariable Long id,
        @Valid @RequestBody PenaltyRuleDto dto
    ) {
        PenaltyRuleDto updated = penaltyService.updatePenaltyRule(id, dto);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/rules/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete penalty rule", description = "Delete a penalty rule")
    public ResponseEntity<Void> deletePenaltyRule(@PathVariable Long id) {
        penaltyService.deletePenaltyRule(id);
        return ResponseEntity.noContent().build();
    }
    
    // ===== Penalty History Management =====
    
    @GetMapping("/history")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all penalty history", description = "Retrieve all penalty history records")
    public ResponseEntity<List<PenaltyHistoryDto>> getAllPenaltyHistory() {
        List<PenaltyHistoryDto> history = penaltyService.getAllPenaltyHistory();
        return ResponseEntity.ok(history);
    }
    
    @GetMapping("/history/user/{userId}")
    @Operation(summary = "Get penalty history by user", description = "Retrieve penalty history for a specific user")
    public ResponseEntity<List<PenaltyHistoryDto>> getPenaltyHistoryByUserId(@PathVariable Long userId) {
        List<PenaltyHistoryDto> history = penaltyService.getPenaltyHistoryByUserId(userId);
        return ResponseEntity.ok(history);
    }
    
    @GetMapping("/history/filter")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get penalty history by date range", description = "Retrieve penalty history within a date range")
    public ResponseEntity<List<PenaltyHistoryDto>> getPenaltyHistoryByDateRange(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        List<PenaltyHistoryDto> history = penaltyService.getPenaltyHistoryByDateRange(startDate, endDate);
        return ResponseEntity.ok(history);
    }
    
    @PostMapping("/apply")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Apply penalty", description = "Apply a penalty to a user by admin")
    public ResponseEntity<PenaltyHistoryDto> applyPenalty(
        @RequestParam Long userId,
        @RequestParam(required = false) Long bookingId,
        @RequestParam Long ruleId,
        @RequestParam String reason,
        Authentication authentication
    ) {
        // Get admin user ID from authentication
        // This is simplified - you'd need to get the actual user ID from the authentication object
        Long adminUserId = 1L; // Replace with actual admin user ID from authentication
        
        PenaltyHistoryDto penalty = penaltyService.applyPenaltyByAdmin(userId, bookingId, ruleId, reason, adminUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(penalty);
    }
    
    @PutMapping("/history/{id}/waive")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Waive penalty", description = "Waive a penalty")
    public ResponseEntity<PenaltyHistoryDto> waivePenalty(
        @PathVariable Long id,
        @RequestParam String notes,
        Authentication authentication
    ) {
        // Get admin user ID from authentication
        Long adminUserId = 1L; // Replace with actual admin user ID from authentication
        
        PenaltyHistoryDto penalty = penaltyService.waivePenalty(id, adminUserId, notes);
        return ResponseEntity.ok(penalty);
    }
    
    @PutMapping("/history/{id}/paid")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Mark penalty as paid", description = "Mark a penalty as paid")
    public ResponseEntity<PenaltyHistoryDto> markAsPaid(@PathVariable Long id) {
        PenaltyHistoryDto penalty = penaltyService.markPenaltyAsPaid(id);
        return ResponseEntity.ok(penalty);
    }
    
    @GetMapping("/users/{userId}/points")
    @Operation(summary = "Get user penalty points", description = "Get total penalty points for a user")
    public ResponseEntity<Integer> getUserPenaltyPoints(@PathVariable Long userId) {
        Integer points = penaltyService.getUserTotalPenaltyPoints(userId);
        return ResponseEntity.ok(points);
    }
}
