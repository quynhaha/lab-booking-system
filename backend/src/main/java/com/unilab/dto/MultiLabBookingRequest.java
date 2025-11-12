package com.unilab.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public class MultiLabBookingRequest {
    
    @NotEmpty(message = "Lab IDs are required")
    private List<Long> labIds;
    
    private Long categoryId;
    
    @NotNull(message = "Title is required")
    private String title;
    
    private String description;
    
    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;
    
    @NotNull(message = "End time is required")
    private LocalDateTime endTime;
    
    private Integer participantsCount;
    
    // Constructors
    public MultiLabBookingRequest() {}
    
    // Getters and Setters
    public List<Long> getLabIds() {
        return labIds;
    }
    
    public void setLabIds(List<Long> labIds) {
        this.labIds = labIds;
    }
    
    public Long getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
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
    
    public Integer getParticipantsCount() {
        return participantsCount;
    }
    
    public void setParticipantsCount(Integer participantsCount) {
        this.participantsCount = participantsCount;
    }
}
