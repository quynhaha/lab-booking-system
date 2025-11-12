package com.unilab.service;

import com.unilab.dto.DashboardDto;
import com.unilab.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {
    
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private LabRepository labRepository;
    
    @Autowired
    private PenaltyHistoryRepository penaltyHistoryRepository;
    
    @Autowired
    private BookingService bookingService;
    
    @Autowired
    private PenaltyService penaltyService;
    
    // Get admin dashboard data
    public DashboardDto getAdminDashboard() {
        DashboardDto dashboard = new DashboardDto();
        
        // Set stats
        DashboardDto.DashboardStats stats = new DashboardDto.DashboardStats();
        stats.setTotalBookings(bookingRepository.count());
        stats.setActiveBookings(bookingRepository.countByStatus("APPROVED"));
        stats.setPendingApprovals(bookingRepository.countByStatus("PENDING"));
        stats.setCompletedBookings(bookingRepository.countByStatus("COMPLETED"));
        stats.setCancelledBookings(bookingRepository.countByStatus("CANCELLED"));
        stats.setTotalUsers(userRepository.count());
        stats.setTotalLabs(labRepository.count());
        stats.setAvailableLabs(labRepository.countByStatus("AVAILABLE"));
        
        // Calculate lab utilization rate (simplified)
        Long totalLabs = labRepository.count();
        Long occupiedLabs = labRepository.countByStatus("OCCUPIED");
        if (totalLabs > 0) {
            stats.setLabUtilizationRate((occupiedLabs.doubleValue() / totalLabs.doubleValue()) * 100);
        } else {
            stats.setLabUtilizationRate(0.0);
        }
        
        stats.setTotalPenalties(penaltyHistoryRepository.count());
        BigDecimal totalPenaltyAmount = penaltyHistoryRepository.sumAmountByStatus("PENDING");
        stats.setTotalPenaltyAmount(totalPenaltyAmount != null ? totalPenaltyAmount.toString() : "0.00");
        
        // Get refund stats
        stats.setPendingRefunds((long) bookingRepository.findPendingRefunds().size());
        BigDecimal pendingRefundAmount = bookingRepository.findPendingRefunds().stream()
            .map(b -> b.getRefundAmount() != null ? b.getRefundAmount() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setPendingRefundAmount(pendingRefundAmount.toString());
        
        dashboard.setStats(stats);
        
        // Set charts data
        Map<String, Object> charts = new HashMap<>();
        // Add chart data here (can be expanded based on requirements)
        dashboard.setCharts(charts);
        
        // Set recent activity
        DashboardDto.RecentActivity recentActivity = new DashboardDto.RecentActivity();
        recentActivity.setRecentBookings(
            bookingService.getAllBookings().stream()
                .limit(10)
                .collect(Collectors.toList())
        );
        recentActivity.setRecentPenalties(
            penaltyService.getAllPenaltyHistory().stream()
                .limit(10)
                .collect(Collectors.toList())
        );
        recentActivity.setPendingApprovals(bookingService.getPendingApprovals());
        
        dashboard.setRecentActivity(recentActivity);
        
        return dashboard;
    }
    
    // Get dashboard filtered by date range
    public DashboardDto getDashboardByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        DashboardDto dashboard = new DashboardDto();
        
        // Set stats for date range
        DashboardDto.DashboardStats stats = new DashboardDto.DashboardStats();
        stats.setTotalBookings(bookingRepository.countByDateRange(startDate, endDate));
        
        // Get bookings in date range and count by status
        var bookingsByStatus = bookingRepository.getBookingStatsByDateRange(startDate, endDate);
        for (Object[] row : bookingsByStatus) {
            String status = (String) row[0];
            Long count = (Long) row[1];
            
            switch (status) {
                case "APPROVED":
                    stats.setActiveBookings(count);
                    break;
                case "PENDING":
                    stats.setPendingApprovals(count);
                    break;
                case "COMPLETED":
                    stats.setCompletedBookings(count);
                    break;
                case "CANCELLED":
                    stats.setCancelledBookings(count);
                    break;
            }
        }
        
        // Get penalties in date range
        var penaltiesInRange = penaltyHistoryRepository.findByDateRange(startDate, endDate);
        stats.setTotalPenalties((long) penaltiesInRange.size());
        
        BigDecimal totalPenaltyAmount = penaltiesInRange.stream()
            .filter(p -> "PENDING".equals(p.getStatus()))
            .map(p -> p.getAmount())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setTotalPenaltyAmount(totalPenaltyAmount.toString());
        
        dashboard.setStats(stats);
        
        // Set charts data
        Map<String, Object> charts = new HashMap<>();
        dashboard.setCharts(charts);
        
        // Set recent activity filtered by date
        DashboardDto.RecentActivity recentActivity = new DashboardDto.RecentActivity();
        recentActivity.setRecentBookings(
            bookingService.getBookingsByDateRange(startDate, endDate).stream()
                .limit(10)
                .collect(Collectors.toList())
        );
        recentActivity.setRecentPenalties(
            penaltyService.getPenaltyHistoryByDateRange(startDate, endDate).stream()
                .limit(10)
                .collect(Collectors.toList())
        );
        
        dashboard.setRecentActivity(recentActivity);
        
        return dashboard;
    }
}
