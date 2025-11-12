package com.unilab.api;

import com.unilab.dto.ReportDto;
import com.unilab.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/admin/reports")
@Tag(name = "Reports", description = "Report generation endpoints for administrators")
@SecurityRequirement(name = "bearerAuth")
public class ReportController {
    
    @Autowired
    private ReportService reportService;
    
    @GetMapping("/bookings")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Generate booking report", description = "Generate detailed report of all bookings within a date range")
    public ResponseEntity<ReportDto> generateBookingReport(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
        Authentication authentication
    ) {
        String userEmail = authentication.getName();
        ReportDto report = reportService.generateBookingReport(startDate, endDate, userEmail);
        return ResponseEntity.ok(report);
    }
    
    @GetMapping("/penalties")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Generate penalty report", description = "Generate detailed report of all penalties within a date range")
    public ResponseEntity<ReportDto> generatePenaltyReport(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
        Authentication authentication
    ) {
        String userEmail = authentication.getName();
        ReportDto report = reportService.generatePenaltyReport(startDate, endDate, userEmail);
        return ResponseEntity.ok(report);
    }
    
    @GetMapping("/lab-utilization")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Generate lab utilization report", description = "Generate report showing lab usage statistics")
    public ResponseEntity<ReportDto> generateLabUtilizationReport(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
        Authentication authentication
    ) {
        String userEmail = authentication.getName();
        ReportDto report = reportService.generateLabUtilizationReport(startDate, endDate, userEmail);
        return ResponseEntity.ok(report);
    }
    
    @GetMapping("/comprehensive")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Generate comprehensive report", description = "Generate comprehensive report with all metrics")
    public ResponseEntity<ReportDto> generateComprehensiveReport(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
        Authentication authentication
    ) {
        String userEmail = authentication.getName();
        ReportDto report = reportService.generateComprehensiveReport(startDate, endDate, userEmail);
        return ResponseEntity.ok(report);
    }
}
