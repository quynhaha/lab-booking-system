package com.unilab.dto;

import java.time.LocalDateTime;

public class BookingLimitResponse {
    
    private Long id;
    private String name;
    private Integer maxBookingsPerUser;
    private Integer maxDurationHours;
    private Integer advanceBookingDays;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public BookingLimitResponse() {}
    
    public BookingLimitResponse(Long id, String name, Integer maxBookingsPerUser, Integer maxDurationHours, 
                              Integer advanceBookingDays, Boolean isActive, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.maxBookingsPerUser = maxBookingsPerUser;
        this.maxDurationHours = maxDurationHours;
        this.advanceBookingDays = advanceBookingDays;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Integer getMaxBookingsPerUser() {
        return maxBookingsPerUser;
    }
    
    public void setMaxBookingsPerUser(Integer maxBookingsPerUser) {
        this.maxBookingsPerUser = maxBookingsPerUser;
    }
    
    public Integer getMaxDurationHours() {
        return maxDurationHours;
    }
    
    public void setMaxDurationHours(Integer maxDurationHours) {
        this.maxDurationHours = maxDurationHours;
    }
    
    public Integer getAdvanceBookingDays() {
        return advanceBookingDays;
    }
    
    public void setAdvanceBookingDays(Integer advanceBookingDays) {
        this.advanceBookingDays = advanceBookingDays;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
