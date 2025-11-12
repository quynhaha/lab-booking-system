package com.unilab.dto;

import java.util.Map;

public class DashboardDto {
    private DashboardStats stats;
    private Map<String, Object> charts;
    private RecentActivity recentActivity;
    
    public static class DashboardStats {
        private Long totalBookings;
        private Long activeBookings;
        private Long pendingApprovals;
        private Long completedBookings;
        private Long cancelledBookings;
        private Long totalUsers;
        private Long activeUsers;
        private Long totalLabs;
        private Long availableLabs;
        private Double labUtilizationRate;
        private Long totalPenalties;
        private String totalPenaltyAmount;
        private Long pendingRefunds;
        private String pendingRefundAmount;
        
        // Getters and Setters
        public Long getTotalBookings() {
            return totalBookings;
        }
        
        public void setTotalBookings(Long totalBookings) {
            this.totalBookings = totalBookings;
        }
        
        public Long getActiveBookings() {
            return activeBookings;
        }
        
        public void setActiveBookings(Long activeBookings) {
            this.activeBookings = activeBookings;
        }
        
        public Long getPendingApprovals() {
            return pendingApprovals;
        }
        
        public void setPendingApprovals(Long pendingApprovals) {
            this.pendingApprovals = pendingApprovals;
        }
        
        public Long getCompletedBookings() {
            return completedBookings;
        }
        
        public void setCompletedBookings(Long completedBookings) {
            this.completedBookings = completedBookings;
        }
        
        public Long getCancelledBookings() {
            return cancelledBookings;
        }
        
        public void setCancelledBookings(Long cancelledBookings) {
            this.cancelledBookings = cancelledBookings;
        }
        
        public Long getTotalUsers() {
            return totalUsers;
        }
        
        public void setTotalUsers(Long totalUsers) {
            this.totalUsers = totalUsers;
        }
        
        public Long getActiveUsers() {
            return activeUsers;
        }
        
        public void setActiveUsers(Long activeUsers) {
            this.activeUsers = activeUsers;
        }
        
        public Long getTotalLabs() {
            return totalLabs;
        }
        
        public void setTotalLabs(Long totalLabs) {
            this.totalLabs = totalLabs;
        }
        
        public Long getAvailableLabs() {
            return availableLabs;
        }
        
        public void setAvailableLabs(Long availableLabs) {
            this.availableLabs = availableLabs;
        }
        
        public Double getLabUtilizationRate() {
            return labUtilizationRate;
        }
        
        public void setLabUtilizationRate(Double labUtilizationRate) {
            this.labUtilizationRate = labUtilizationRate;
        }
        
        public Long getTotalPenalties() {
            return totalPenalties;
        }
        
        public void setTotalPenalties(Long totalPenalties) {
            this.totalPenalties = totalPenalties;
        }
        
        public String getTotalPenaltyAmount() {
            return totalPenaltyAmount;
        }
        
        public void setTotalPenaltyAmount(String totalPenaltyAmount) {
            this.totalPenaltyAmount = totalPenaltyAmount;
        }
        
        public Long getPendingRefunds() {
            return pendingRefunds;
        }
        
        public void setPendingRefunds(Long pendingRefunds) {
            this.pendingRefunds = pendingRefunds;
        }
        
        public String getPendingRefundAmount() {
            return pendingRefundAmount;
        }
        
        public void setPendingRefundAmount(String pendingRefundAmount) {
            this.pendingRefundAmount = pendingRefundAmount;
        }
    }
    
    public static class RecentActivity {
        private java.util.List<BookingDto> recentBookings;
        private java.util.List<PenaltyHistoryDto> recentPenalties;
        private java.util.List<BookingDto> pendingApprovals;
        
        // Getters and Setters
        public java.util.List<BookingDto> getRecentBookings() {
            return recentBookings;
        }
        
        public void setRecentBookings(java.util.List<BookingDto> recentBookings) {
            this.recentBookings = recentBookings;
        }
        
        public java.util.List<PenaltyHistoryDto> getRecentPenalties() {
            return recentPenalties;
        }
        
        public void setRecentPenalties(java.util.List<PenaltyHistoryDto> recentPenalties) {
            this.recentPenalties = recentPenalties;
        }
        
        public java.util.List<BookingDto> getPendingApprovals() {
            return pendingApprovals;
        }
        
        public void setPendingApprovals(java.util.List<BookingDto> pendingApprovals) {
            this.pendingApprovals = pendingApprovals;
        }
    }
    
    // Constructors
    public DashboardDto() {}
    
    // Getters and Setters
    public DashboardStats getStats() {
        return stats;
    }
    
    public void setStats(DashboardStats stats) {
        this.stats = stats;
    }
    
    public Map<String, Object> getCharts() {
        return charts;
    }
    
    public void setCharts(Map<String, Object> charts) {
        this.charts = charts;
    }
    
    public RecentActivity getRecentActivity() {
        return recentActivity;
    }
    
    public void setRecentActivity(RecentActivity recentActivity) {
        this.recentActivity = recentActivity;
    }
}
