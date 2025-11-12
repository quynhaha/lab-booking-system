package com.unilab.dto;

import jakarta.validation.constraints.NotNull;

public class CancelBookingRequest {
    
    @NotNull(message = "Booking ID is required")
    private Long bookingId;
    
    private String cancellationReason;
    
    private Boolean withRefund;
    
    // Constructors
    public CancelBookingRequest() {}
    
    public CancelBookingRequest(Long bookingId, String cancellationReason, Boolean withRefund) {
        this.bookingId = bookingId;
        this.cancellationReason = cancellationReason;
        this.withRefund = withRefund;
    }
    
    // Getters and Setters
    public Long getBookingId() {
        return bookingId;
    }
    
    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }
    
    public String getCancellationReason() {
        return cancellationReason;
    }
    
    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }
    
    public Boolean getWithRefund() {
        return withRefund;
    }
    
    public void setWithRefund(Boolean withRefund) {
        this.withRefund = withRefund;
    }
}
