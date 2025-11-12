package com.unilab.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "booking_rules")
public class BookingRule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String code;
    
    @Column(nullable = false)
    private String name;
    
    @Column(length = 1000)
    private String description;
    
    @Column(name = "rule_type", nullable = false)
    private String ruleType; // TIME_LIMIT, ADVANCE_BOOKING, MAX_DURATION, CAPACITY, etc.
    
    @Column(name = "rule_value", nullable = false)
    private String ruleValue; // JSON or simple value
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(nullable = false)
    private Integer priority; // Order of rule application
    
    @Column(name = "applies_to")
    private String appliesTo; // ALL, STUDENT, FACULTY, SPECIFIC_CATEGORY
    
    @Column(name = "category_id")
    private Long categoryId; // If rule applies to specific category
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Constructors
    public BookingRule() {}
    
    public BookingRule(String code, String name, String ruleType, String ruleValue, Integer priority) {
        this.code = code;
        this.name = name;
        this.ruleType = ruleType;
        this.ruleValue = ruleValue;
        this.priority = priority;
    }
    
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
