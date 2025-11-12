package com.unilab.dto;

import java.time.LocalDateTime;

public class PenaltyHistoryDto {
    private Long id;
    private Long userId;
    private String userEmail;
    private String userName;
    private Long bookingId;
    private String bookingCode;
    private Long penaltyRuleId;
    private String penaltyRuleName;
    private String violationType;
    private String amount;
    private Integer penaltyPoints;
    private String status;
    private String reason;
    private String notes;
    private Long appliedByUserId;
    private String appliedByUserName;
    private Long waivedByUserId;
    private String waivedByUserName;
    private LocalDateTime paidAt;
    private LocalDateTime waivedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public PenaltyHistoryDto() {}
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getUserEmail() {
        return userEmail;
    }
    
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public Long getBookingId() {
        return bookingId;
    }
    
    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }
    
    public String getBookingCode() {
        return bookingCode;
    }
    
    public void setBookingCode(String bookingCode) {
        this.bookingCode = bookingCode;
    }
    
    public Long getPenaltyRuleId() {
        return penaltyRuleId;
    }
    
    public void setPenaltyRuleId(Long penaltyRuleId) {
        this.penaltyRuleId = penaltyRuleId;
    }
    
    public String getPenaltyRuleName() {
        return penaltyRuleName;
    }
    
    public void setPenaltyRuleName(String penaltyRuleName) {
        this.penaltyRuleName = penaltyRuleName;
    }
    
    public String getViolationType() {
        return violationType;
    }
    
    public void setViolationType(String violationType) {
        this.violationType = violationType;
    }
    
    public String getAmount() {
        return amount;
    }
    
    public void setAmount(String amount) {
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
    
    public String getAppliedByUserName() {
        return appliedByUserName;
    }
    
    public void setAppliedByUserName(String appliedByUserName) {
        this.appliedByUserName = appliedByUserName;
    }
    
    public Long getWaivedByUserId() {
        return waivedByUserId;
    }
    
    public void setWaivedByUserId(Long waivedByUserId) {
        this.waivedByUserId = waivedByUserId;
    }
    
    public String getWaivedByUserName() {
        return waivedByUserName;
    }
    
    public void setWaivedByUserName(String waivedByUserName) {
        this.waivedByUserName = waivedByUserName;
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
