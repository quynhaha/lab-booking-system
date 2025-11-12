package com.unilab.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ActivateBookingLimitRequest {

    @NotNull(message = "Booking limit ID is required")
    @Positive(message = "Booking limit ID must be positive")
    private Long id;

    public ActivateBookingLimitRequest() {}

    public ActivateBookingLimitRequest(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

