package com.unilab.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class DeleteLabRequest {

    @NotNull(message = "Lab ID is required")
    @Positive(message = "Lab ID must be positive")
    private Long labId;

    public DeleteLabRequest() {}

    public DeleteLabRequest(Long labId) {
        this.labId = labId;
    }

    public Long getLabId() {
        return labId;
    }

    public void setLabId(Long labId) {
        this.labId = labId;
    }
}

