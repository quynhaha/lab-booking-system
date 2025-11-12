package com.unilab.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class UpdateLabRequest {

    @NotNull(message = "Lab ID is required")
    @Positive(message = "Lab ID must be positive")
    private Long labId;

    @NotBlank(message = "Lab name is required")
    @Size(max = 100, message = "Lab name must not exceed 100 characters")
    private String name;

    @Positive(message = "Capacity must be positive")
    private Integer capacity;

    @Size(max = 100, message = "Location must not exceed 100 characters")
    private String location;

    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;

    @Size(max = 20, message = "Status must not exceed 20 characters")
    private String status;

    public Long getLabId() {
        return labId;
    }

    public void setLabId(Long labId) {
        this.labId = labId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

