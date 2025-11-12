package com.unilab.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "penalty_rules")
public class PenaltyRule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String code;
    
    @Column(nullable = false)
    private String name;
    
    @Column(length = 1000)
    private String description;
    
    @Column(nullable = false)
    private String violationType; // LATE_CANCELLATION, NO_SHOW, DAMAGE, POLICY_VIOLATION, etc.
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal penaltyAmount;
    
    @Column(name = "penalty_type", nullable = false)
    private String penaltyType; // FIXED, PERCENTAGE, POINTS
    
    @Column(name = "penalty_points")
    private Integer penaltyPoints; // For point-based system
    
    @Column(name = "suspension_days")
    private Integer suspensionDays; // Number of days user is suspended from booking
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "grace_period_hours")
    private Integer gracePeriodHours; // Hours before event when penalty applies
    
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
    public PenaltyRule() {}
    
    public PenaltyRule(String code, String name, String violationType, BigDecimal penaltyAmount, String penaltyType) {
        this.code = code;
        this.name = name;
        this.violationType = violationType;
        this.penaltyAmount = penaltyAmount;
        this.penaltyType = penaltyType;
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
    
    public String getViolationType() {
        return violationType;
    }
    
    public void setViolationType(String violationType) {
        this.violationType = violationType;
    }
    
    public BigDecimal getPenaltyAmount() {
        return penaltyAmount;
    }
    
    public void setPenaltyAmount(BigDecimal penaltyAmount) {
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
