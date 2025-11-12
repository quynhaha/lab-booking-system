package com.unilab.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class UpdateBookingLimitRequest {

    @NotBlank(message = "Limit name is required")
    @Size(max = 100, message = "Limit name must not exceed 100 characters")
    private String name;
    
    @NotNull(message = "Max bookings per user is required")
    @Min(value = 1, message = "Max bookings per user must be at least 1")
    private Integer maxBookingsPerUser;
    
    @NotNull(message = "Max duration hours is required")
    @Min(value = 1, message = "Max duration hours must be at least 1")
    private Integer maxDurationHours;
    
    @NotNull(message = "Advance booking days is required")
    @Min(value = 1, message = "Advance booking days must be at least 1")
    private Integer advanceBookingDays;

    private Boolean isActive;

    public UpdateBookingLimitRequest() {}

    public UpdateBookingLimitRequest(String name, Integer maxBookingsPerUser, Integer maxDurationHours, Integer advanceBookingDays, Boolean isActive) {
        this.name = name;
        this.maxBookingsPerUser = maxBookingsPerUser;
        this.maxDurationHours = maxDurationHours;
        this.advanceBookingDays = advanceBookingDays;
        this.isActive = isActive;
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
}
