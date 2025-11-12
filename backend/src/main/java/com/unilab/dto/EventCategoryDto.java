package com.unilab.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public class EventCategoryDto {
    private Long id;
    
    @NotBlank(message = "Category code is required")
    @Size(max = 50, message = "Category code must not exceed 50 characters")
    private String code;
    
    @NotBlank(message = "Category name is required")
    @Size(max = 100, message = "Category name must not exceed 100 characters")
    private String name;
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
    
    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Color must be a valid hex color code (e.g., #FF5733)")
    private String color;
    
    private Boolean isActive;
    
    @Min(value = 1, message = "Max duration must be at least 1 hour")
    @Max(value = 168, message = "Max duration must not exceed 168 hours (7 days)")
    private Integer maxDurationHours;
    
    private Boolean requiresApproval;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public EventCategoryDto() {}
    
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
    
    public String getColor() {
        return color;
    }
    
    public void setColor(String color) {
        this.color = color;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public Integer getMaxDurationHours() {
        return maxDurationHours;
    }
    
    public void setMaxDurationHours(Integer maxDurationHours) {
        this.maxDurationHours = maxDurationHours;
    }
    
    public Boolean getRequiresApproval() {
        return requiresApproval;
    }
    
    public void setRequiresApproval(Boolean requiresApproval) {
        this.requiresApproval = requiresApproval;
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
