package com.unilab.service;

import com.unilab.dto.ReportDto;
import com.unilab.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ReportService {
    
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private LabRepository labRepository;
    
    @Autowired
    private PenaltyHistoryRepository penaltyHistoryRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    // Generate booking report
    public ReportDto generateBookingReport(LocalDateTime startDate, LocalDateTime endDate, String userEmail) {
        ReportDto report = new ReportDto("BOOKING_REPORT", startDate, endDate);
        report.setGeneratedBy(userEmail);
        
        var bookings = bookingRepository.findByDateRange(startDate, endDate);
        
        // Summary
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalBookings", bookings.size());
        summary.put("approvedBookings", bookings.stream().filter(b -> "APPROVED".equals(b.getStatus())).count());
        summary.put("pendingBookings", bookings.stream().filter(b -> "PENDING".equals(b.getStatus())).count());
        summary.put("cancelledBookings", bookings.stream().filter(b -> "CANCELLED".equals(b.getStatus())).count());
        summary.put("completedBookings", bookings.stream().filter(b -> "COMPLETED".equals(b.getStatus())).count());
        report.setSummary(summary);
        
        // Detailed data
        List<Map<String, Object>> data = new ArrayList<>();
        for (var booking : bookings) {
            Map<String, Object> row = new HashMap<>();
            row.put("bookingCode", booking.getBookingCode());
            row.put("labName", booking.getLab().getName());
            row.put("userEmail", booking.getUser().getEmail());
            row.put("title", booking.getTitle());
            row.put("startTime", booking.getStartTime());
            row.put("endTime", booking.getEndTime());
            row.put("status", booking.getStatus());
            row.put("category", booking.getCategory() != null ? booking.getCategory().getName() : "N/A");
            data.add(row);
        }
        report.setData(data);
        
        // Charts data
        Map<String, Object> charts = new HashMap<>();
        
        // Bookings by status
        Map<String, Long> byStatus = new HashMap<>();
        byStatus.put("APPROVED", bookings.stream().filter(b -> "APPROVED".equals(b.getStatus())).count());
        byStatus.put("PENDING", bookings.stream().filter(b -> "PENDING".equals(b.getStatus())).count());
        byStatus.put("CANCELLED", bookings.stream().filter(b -> "CANCELLED".equals(b.getStatus())).count());
        byStatus.put("COMPLETED", bookings.stream().filter(b -> "COMPLETED".equals(b.getStatus())).count());
        charts.put("bookingsByStatus", byStatus);
        
        // Bookings by lab
        Map<String, Long> byLab = new HashMap<>();
        bookings.forEach(b -> {
            String labName = b.getLab().getName();
            byLab.put(labName, byLab.getOrDefault(labName, 0L) + 1);
        });
        charts.put("bookingsByLab", byLab);
        
        report.setCharts(charts);
        
        return report;
    }
    
    // Generate penalty report
    public ReportDto generatePenaltyReport(LocalDateTime startDate, LocalDateTime endDate, String userEmail) {
        ReportDto report = new ReportDto("PENALTY_REPORT", startDate, endDate);
        report.setGeneratedBy(userEmail);
        
        var penalties = penaltyHistoryRepository.findByDateRange(startDate, endDate);
        
        // Summary
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalPenalties", penalties.size());
        summary.put("pendingPenalties", penalties.stream().filter(p -> "PENDING".equals(p.getStatus())).count());
        summary.put("paidPenalties", penalties.stream().filter(p -> "PAID".equals(p.getStatus())).count());
        summary.put("waivedPenalties", penalties.stream().filter(p -> "WAIVED".equals(p.getStatus())).count());
        
        var totalAmount = penalties.stream()
            .map(p -> p.getAmount())
            .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        summary.put("totalAmount", totalAmount.toString());
        
        report.setSummary(summary);
        
        // Detailed data
        List<Map<String, Object>> data = new ArrayList<>();
        for (var penalty : penalties) {
            Map<String, Object> row = new HashMap<>();
            row.put("userEmail", penalty.getUser().getEmail());
            row.put("violationType", penalty.getPenaltyRule().getViolationType());
            row.put("amount", penalty.getAmount().toString());
            row.put("penaltyPoints", penalty.getPenaltyPoints());
            row.put("status", penalty.getStatus());
            row.put("reason", penalty.getReason());
            row.put("createdAt", penalty.getCreatedAt());
            data.add(row);
        }
        report.setData(data);
        
        // Charts data
        Map<String, Object> charts = new HashMap<>();
        
        // Penalties by violation type
        var penaltyStats = penaltyHistoryRepository.getPenaltyStatsByTypeAndDateRange(startDate, endDate);
        Map<String, Long> byType = new HashMap<>();
        for (Object[] row : penaltyStats) {
            String type = (String) row[0];
            Long count = (Long) row[1];
            byType.put(type, count);
        }
        charts.put("penaltiesByType", byType);
        
        report.setCharts(charts);
        
        return report;
    }
    
    // Generate lab utilization report
    public ReportDto generateLabUtilizationReport(LocalDateTime startDate, LocalDateTime endDate, String userEmail) {
        ReportDto report = new ReportDto("LAB_UTILIZATION_REPORT", startDate, endDate);
        report.setGeneratedBy(userEmail);
        
        var labs = labRepository.findAll();
        var bookings = bookingRepository.findByDateRange(startDate, endDate);
        
        // Summary
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalLabs", labs.size());
        summary.put("totalBookings", bookings.size());
        
        report.setSummary(summary);
        
        // Detailed data - utilization by lab
        List<Map<String, Object>> data = new ArrayList<>();
        for (var lab : labs) {
            long labBookingCount = bookings.stream()
                .filter(b -> b.getLab().getId().equals(lab.getId()))
                .count();
            
            Map<String, Object> row = new HashMap<>();
            row.put("labId", lab.getId());
            row.put("labName", lab.getName());
            row.put("location", lab.getLocation());
            row.put("capacity", lab.getCapacity());
            row.put("bookingCount", labBookingCount);
            row.put("status", lab.getStatus());
            data.add(row);
        }
        report.setData(data);
        
        // Charts data
        Map<String, Object> charts = new HashMap<>();
        
        // Bookings by lab
        Map<String, Long> bookingsByLab = new HashMap<>();
        bookings.forEach(b -> {
            String labName = b.getLab().getName();
            bookingsByLab.put(labName, bookingsByLab.getOrDefault(labName, 0L) + 1);
        });
        charts.put("bookingsByLab", bookingsByLab);
        
        report.setCharts(charts);
        
        return report;
    }
    
    // Generate comprehensive admin report
    public ReportDto generateComprehensiveReport(LocalDateTime startDate, LocalDateTime endDate, String userEmail) {
        ReportDto report = new ReportDto("COMPREHENSIVE_REPORT", startDate, endDate);
        report.setGeneratedBy(userEmail);
        
        var bookings = bookingRepository.findByDateRange(startDate, endDate);
        var penalties = penaltyHistoryRepository.findByDateRange(startDate, endDate);
        var labs = labRepository.findAll();
        
        // Summary with all key metrics
        Map<String, Object> summary = new HashMap<>();
        
        // Booking metrics
        summary.put("totalBookings", bookings.size());
        summary.put("approvedBookings", bookings.stream().filter(b -> "APPROVED".equals(b.getStatus())).count());
        summary.put("pendingBookings", bookings.stream().filter(b -> "PENDING".equals(b.getStatus())).count());
        summary.put("cancelledBookings", bookings.stream().filter(b -> "CANCELLED".equals(b.getStatus())).count());
        
        // Lab metrics
        summary.put("totalLabs", labs.size());
        summary.put("availableLabs", labs.stream().filter(l -> "AVAILABLE".equals(l.getStatus())).count());
        
        // Penalty metrics
        summary.put("totalPenalties", penalties.size());
        var totalPenaltyAmount = penalties.stream()
            .map(p -> p.getAmount())
            .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        summary.put("totalPenaltyAmount", totalPenaltyAmount.toString());
        
        // User metrics
        summary.put("totalUsers", userRepository.count());
        
        report.setSummary(summary);
        
        // Combined data
        List<Map<String, Object>> data = new ArrayList<>();
        // Add comprehensive data as needed
        report.setData(data);
        
        // Charts
        Map<String, Object> charts = new HashMap<>();
        report.setCharts(charts);
        
        return report;
    }
}
