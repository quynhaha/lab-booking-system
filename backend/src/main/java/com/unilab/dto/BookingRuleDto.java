package com.unilab.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public class BookingRuleDto {
    private Long id;
    
    @NotBlank(message = "Rule code is required")
    @Size(max = 50, message = "Rule code must not exceed 50 characters")
    private String code;
    
    @NotBlank(message = "Rule name is required")
    @Size(max = 200, message = "Rule name must not exceed 200 characters")
    private String name;
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
    
    @NotBlank(message = "Rule type is required")
    @Pattern(regexp = "MAX_ADVANCE_DAYS|MIN_ADVANCE_HOURS|MAX_DURATION|MIN_DURATION|MAX_CONCURRENT", 
             message = "Invalid rule type")
    private String ruleType;
    
    @NotBlank(message = "Rule value is required")
    private String ruleValue;
    
    private Boolean isActive;
    
    @Min(value = 1, message = "Priority must be at least 1")
    @Max(value = 100, message = "Priority must not exceed 100")
    private Integer priority;
    
    @Pattern(regexp = "ALL|STUDENT|STAFF", message = "Applies to must be ALL, STUDENT, or STAFF")
    private String appliesTo;
    
    private Long categoryId;
    private String categoryName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public BookingRuleDto() {}
    
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
    
    public String getRuleType() {
        return ruleType;
    }
    
    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }
    
    public String getRuleValue() {
        return ruleValue;
    }
    
    public void setRuleValue(String ruleValue) {
        this.ruleValue = ruleValue;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public Integer getPriority() {
        return priority;
    }
    
    public void setPriority(Integer priority) {
        this.priority = priority;
    }
    
    public String getAppliesTo() {
        return appliesTo;
    }
    
    public void setAppliesTo(String appliesTo) {
        this.appliesTo = appliesTo;
    }
    
    public Long getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
    
    public String getCategoryName() {
        return categoryName;
    }
    
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
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
