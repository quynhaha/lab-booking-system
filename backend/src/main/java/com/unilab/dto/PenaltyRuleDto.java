package com.unilab.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public class PenaltyRuleDto {
    private Long id;
    
    @NotBlank(message = "Penalty code is required")
    @Size(max = 50, message = "Penalty code must not exceed 50 characters")
    private String code;
    
    @NotBlank(message = "Penalty name is required")
    @Size(max = 200, message = "Penalty name must not exceed 200 characters")
    private String name;
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
    
    @NotBlank(message = "Violation type is required")
    @Pattern(regexp = "LATE_CANCELLATION|NO_SHOW|EQUIPMENT_DAMAGE|FACILITY_MISUSE|REPEATED_VIOLATION", 
             message = "Invalid violation type")
    private String violationType;
    
    @NotBlank(message = "Penalty amount is required")
    private String penaltyAmount;
    
    @NotBlank(message = "Penalty type is required")
    @Pattern(regexp = "FINE|SUSPENSION|WARNING|POINTS", message = "Penalty type must be FINE, SUSPENSION, WARNING, or POINTS")
    private String penaltyType;
    
    @Min(value = 0, message = "Penalty points must be non-negative")
    @Max(value = 100, message = "Penalty points must not exceed 100")
    private Integer penaltyPoints;
    
    @Min(value = 0, message = "Suspension days must be non-negative")
    @Max(value = 365, message = "Suspension days must not exceed 365")
    private Integer suspensionDays;
    
    private Boolean isActive;
    
    @Min(value = 0, message = "Grace period must be non-negative")
    private Integer gracePeriodHours;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public PenaltyRuleDto() {}
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getViolationType() {
        return violationType;
    }
    
    public void setViolationType(String violationType) {
        this.violationType = violationType;
    }
    
    public String getPenaltyAmount() {
        return penaltyAmount;
    }
    
    public void setPenaltyAmount(String penaltyAmount) {
        this.penaltyAmount = penaltyAmount;
    }
    
    public String getPenaltyType() {
        return penaltyType;
    }
    
    public void setPenaltyType(String penaltyType) {
        this.penaltyType = penaltyType;
    }
    
    public Integer getPenaltyPoints() {
        return penaltyPoints;
    }
    
    public void setPenaltyPoints(Integer penaltyPoints) {
        this.penaltyPoints = penaltyPoints;
    }
    
    public Integer getSuspensionDays() {
        return suspensionDays;
    }
    
    public void setSuspensionDays(Integer suspensionDays) {
        this.suspensionDays = suspensionDays;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public Integer getGracePeriodHours() {
        return gracePeriodHours;
    }
    
    public void setGracePeriodHours(Integer gracePeriodHours) {
        this.gracePeriodHours = gracePeriodHours;
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
