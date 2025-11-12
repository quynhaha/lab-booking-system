package com.unilab.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bookings", indexes = {
    @Index(name = "idx_booking_status", columnList = "status"),
    @Index(name = "idx_booking_user", columnList = "user_id"),
    @Index(name = "idx_booking_dates", columnList = "start_time, end_time")
})
public class Booking {

    @Transient  // This field is not in DB, ignore it
    private String rejectionReason;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String bookingCode;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lab_id", nullable = false)
    private Lab lab;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private EventCategory category;
    
    @Column(nullable = false)
    private String title;
    
    @Column(length = 2000)
    private String description;
    
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;
    
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;
    
    @Column(nullable = false)
    private String status; // PENDING, APPROVED, REJECTED, CANCELLED, COMPLETED
    
    @Column(name = "participants_count")
    private Integer participantsCount;
    
    @Column(name = "is_multi_lab")
    private Boolean isMultiLab = false;
    
    @Column(name = "parent_booking_id")
    private Long parentBookingId; // For multi-lab bookings
    
    @Column(name = "refund_amount", precision = 10, scale = 2)
    private BigDecimal refundAmount;
    
    @Column(name = "refund_status")
    private String refundStatus; // NONE, PENDING, PROCESSED, REJECTED
    
    @Column(name = "cancellation_reason", length = 1000)
    private String cancellationReason;
    
    @Column(name = "cancelled_by_user_id")
    private Long cancelledByUserId;
    
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;
    
    @Column(name = "approved_by_user_id")
    private Long approvedByUserId;
    
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PenaltyHistory> penalties = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (refundStatus == null) {
            refundStatus = "NONE";
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Constructors
    public Booking() {}
    
    public Booking(String bookingCode, User user, Lab lab, String title, LocalDateTime startTime, LocalDateTime endTime, String status) {
        this.bookingCode = bookingCode;
        this.user = user;
        this.lab = lab;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getBookingCode() {
        return bookingCode;
    }
    
    public void setBookingCode(String bookingCode) {
        this.bookingCode = bookingCode;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Lab getLab() {
        return lab;
    }
    
    public void setLab(Lab lab) {
        this.lab = lab;
    }
    
    public EventCategory getCategory() {
        return category;
    }
    
    public void setCategory(EventCategory category) {
        this.category = category;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    
    public LocalDateTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Integer getParticipantsCount() {
        return participantsCount;
    }
    
    public void setParticipantsCount(Integer participantsCount) {
        this.participantsCount = participantsCount;
    }
    
    public Boolean getIsMultiLab() {
        return isMultiLab;
    }
    
    public void setIsMultiLab(Boolean isMultiLab) {
        this.isMultiLab = isMultiLab;
    }
    
    public Long getParentBookingId() {
        return parentBookingId;
    }
    
    public void setParentBookingId(Long parentBookingId) {
        this.parentBookingId = parentBookingId;
    }
    
    public BigDecimal getRefundAmount() {
        return refundAmount;
    }
    
    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }
    
    public String getRefundStatus() {
        return refundStatus;
    }
    
    public void setRefundStatus(String refundStatus) {
        this.refundStatus = refundStatus;
    }
    
    public String getCancellationReason() {
        return cancellationReason;
    }
    
    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }
    
    public Long getCancelledByUserId() {
        return cancelledByUserId;
    }
    
    public void setCancelledByUserId(Long cancelledByUserId) {
        this.cancelledByUserId = cancelledByUserId;
    }
    
    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }
    
    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }
    
    public Long getApprovedByUserId() {
        return approvedByUserId;
    }
    
    public void setApprovedByUserId(Long approvedByUserId) {
        this.approvedByUserId = approvedByUserId;
    }
    
    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }
    
    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
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
    
    public List<PenaltyHistory> getPenalties() {
        return penalties;
    }
    
    public void setPenalties(List<PenaltyHistory> penalties) {
        this.penalties = penalties;
    }

    public String getRejectionReason() { return rejectionReason; }

    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
}
