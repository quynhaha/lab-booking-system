package com.unilab.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "booking_limits")
public class BookingLimit {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "limit_id")
    private Long id;
    
    @Column(name = "limit_name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "max_bookings_per_user", nullable = false)
    private Integer maxBookingsPerUser;
    
    @Column(name = "max_duration_hours", nullable = false)
    private Integer maxDurationHours;
    
    @Column(name = "advance_booking_days", nullable = false)
    private Integer advanceBookingDays;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public BookingLimit() {}
    
    public BookingLimit(String name, Integer maxBookingsPerUser, Integer maxDurationHours, Integer advanceBookingDays) {
        this.name = name;
        this.maxBookingsPerUser = maxBookingsPerUser;
        this.maxDurationHours = maxDurationHours;
        this.advanceBookingDays = advanceBookingDays;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
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
