package com.unilab.dto;

import jakarta.validation.constraints.NotNull;

public class AttachEventRequest {

    @NotNull(message = "eventId is required")
    private Long eventId;

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }
}

