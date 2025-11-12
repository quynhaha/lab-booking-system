package com.unilab.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO for slot availability information
 */
@Schema(description = "Slot availability information")
public class SlotAvailabilityDto {
    
    @Schema(description = "Slot number (1-4)", example = "1")
    private Integer slotNumber;
    
    @Schema(description = "Slot name/display name", example = "Ca 1: Sáng (08:00 - 11:00)")
    private String slotName;
    
    @Schema(description = "Start time of the slot", example = "08:00")
    private String startTime;
    
    @Schema(description = "End time of the slot", example = "11:00")
    private String endTime;
    
    @Schema(description = "Whether the slot is available for booking", example = "true")
    private Boolean isAvailable;
    
    @Schema(description = "Reason if slot is not available", example = "Already booked")
    private String reason;
    
    // Constructors
    public SlotAvailabilityDto() {}
    
    public SlotAvailabilityDto(Integer slotNumber, String slotName, String startTime, String endTime, Boolean isAvailable) {
        this.slotNumber = slotNumber;
        this.slotName = slotName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isAvailable = isAvailable;
    }
    
    // Getters and Setters
    public Integer getSlotNumber() {
        return slotNumber;
    }
    
    public void setSlotNumber(Integer slotNumber) {
        this.slotNumber = slotNumber;
    }
    
    public String getSlotName() {
        return slotName;
    }
    
    public void setSlotName(String slotName) {
        this.slotName = slotName;
    }
    
    public String getStartTime() {
        return startTime;
    }
    
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    
    public String getEndTime() {
        return endTime;
    }
    
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
    
    public Boolean getIsAvailable() {
        return isAvailable;
    }
    
    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
}

