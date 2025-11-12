package com.unilab.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "penalty_history", indexes = {
    @Index(name = "idx_penalty_user", columnList = "user_id"),
    @Index(name = "idx_penalty_booking", columnList = "booking_id"),
    @Index(name = "idx_penalty_status", columnList = "status")
})
public class PenaltyHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "penalty_rule_id", nullable = false)
    private PenaltyRule penaltyRule;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    
    @Column(name = "penalty_points")
    private Integer penaltyPoints;
    
    @Column(nullable = false)
    private String status; // PENDING, PAID, WAIVED, APPEALED
    
    @Column(length = 2000)
    private String reason;
    
    @Column(length = 2000)
    private String notes;
    
    @Column(name = "applied_by_user_id")
    private Long appliedByUserId; // Admin who applied the penalty
    
    @Column(name = "waived_by_user_id")
    private Long waivedByUserId; // Admin who waived the penalty
    
    @Column(name = "paid_at")
    private LocalDateTime paidAt;
    
    @Column(name = "waived_at")
    private LocalDateTime waivedAt;
    
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
    public PenaltyHistory() {}
    
    public PenaltyHistory(User user, Booking booking, PenaltyRule penaltyRule, BigDecimal amount, String status) {
        this.user = user;
        this.booking = booking;
        this.penaltyRule = penaltyRule;
        this.amount = amount;
        this.status = status;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Booking getBooking() {
        return booking;
    }
    
    public void setBooking(Booking booking) {
        this.booking = booking;
    }
    
    public PenaltyRule getPenaltyRule() {
        return penaltyRule;
    }
    
    public void setPenaltyRule(PenaltyRule penaltyRule) {
        this.penaltyRule = penaltyRule;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public Integer getPenaltyPoints() {
        return penaltyPoints;
    }
    
    public void setPenaltyPoints(Integer penaltyPoints) {
        this.penaltyPoints = penaltyPoints;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public Long getAppliedByUserId() {
        return appliedByUserId;
    }
    
    public void setAppliedByUserId(Long appliedByUserId) {
        this.appliedByUserId = appliedByUserId;
    }
    
    public Long getWaivedByUserId() {
        return waivedByUserId;
    }
    
    public void setWaivedByUserId(Long waivedByUserId) {
        this.waivedByUserId = waivedByUserId;
    }
    
    public LocalDateTime getPaidAt() {
        return paidAt;
    }
    
    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }
    
    public LocalDateTime getWaivedAt() {
        return waivedAt;
    }
    
    public void setWaivedAt(LocalDateTime waivedAt) {
        this.waivedAt = waivedAt;
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
