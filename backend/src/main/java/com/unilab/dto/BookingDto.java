package com.unilab.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;
import com.unilab.model.Booking;

public class BookingDto {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String bookingCode;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String status;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String userEmail;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String userName;

    @Schema(description = "User ID (auto-filled from login token)", accessMode = Schema.AccessMode.READ_ONLY)
    private Long userId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long labId;

    @Schema(description = "List of Lab IDs for multi-lab booking")
    private List<LabBookingSlotDto> labSlots;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String labName;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String labCode;

    @Schema(description = "Event category ID (optional)")
    private Long categoryId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String categoryName;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String title;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String description;

    //@NotNull(message = "Start time is required")
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime startTime;

    //@NotNull(message = "End time is required")
    @Schema(example = "2025-10-26T10:00:00")
    private LocalDateTime endTime;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Integer participantsCount;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean isMultiLab;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long parentBookingId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private List<Long> childBookingIds;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String refundStatus;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String refundAmount;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String cancellationReason;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long cancelledByUserId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String cancelledByUserName;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime cancelledAt;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long approvedByUserId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String approvedByUserName;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime approvedAt;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;

    // Constructors
    public BookingDto() {}

    // Getters and Setters
    public List<LabBookingSlotDto> getLabSlots() {
        return labSlots;
    }

    public void setLabSlots(List<LabBookingSlotDto> labSlots) {
        this.labSlots = labSlots;
    }

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

    public Long getLabId() {
        return labId;
    }

    public void setLabId(Long labId) {
        this.labId = labId;
    }

    public String getLabName() {
        return labName;
    }

    public void setLabName(String labName) {
        this.labName = labName;
    }

    public String getLabCode() {
        return labCode;
    }

    public void setLabCode(String labCode) {
        this.labCode = labCode;
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

    public List<Long> getChildBookingIds() {
        return childBookingIds;
    }

    public void setChildBookingIds(List<Long> childBookingIds) {
        this.childBookingIds = childBookingIds;
    }

    public String getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(String refundStatus) {
        this.refundStatus = refundStatus;
    }

    public String getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(String refundAmount) {
        this.refundAmount = refundAmount;
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

    public String getCancelledByUserName() {
        return cancelledByUserName;
    }

    public void setCancelledByUserName(String cancelledByUserName) {
        this.cancelledByUserName = cancelledByUserName;
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

    public String getApprovedByUserName() {
        return approvedByUserName;
    }

    public void setApprovedByUserName(String approvedByUserName) {
        this.approvedByUserName = approvedByUserName;
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

    public static BookingDto fromEntity(Booking booking) {
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setBookingCode(booking.getBookingCode());
        dto.setTitle(booking.getTitle());
        dto.setDescription(booking.getDescription());
        dto.setStartTime(booking.getStartTime());
        dto.setEndTime(booking.getEndTime());
        dto.setStatus(booking.getStatus());
        dto.setParticipantsCount(booking.getParticipantsCount());
        return dto;
    }
}