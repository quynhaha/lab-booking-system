package com.unilab.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import java.time.LocalDateTime;

@Schema(description = "Represents a single lab booking slot with its own time and details")
public class LabBookingSlotDto {

    @NotNull(message = "Lab ID is required")
    @Schema(description = "ID of the lab to be booked", example = "6", required = true)
    private Long labId;

    @NotNull(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    @Schema(description = "Title or topic for this booking slot", example = "AI Fundamentals Workshop")
    private String title;

    @Schema(description = "Optional description about the session", example = "Introduction to neural networks using TensorFlow")
    private String description;

    @NotNull(message = "Participants count is required")
    @Positive(message = "Participants count must be greater than 0")
    @Schema(description = "Number of participants expected", example = "25")
    private Integer participantsCount;

    @NotNull(message = "Slot number is required")
    @Schema(description = "Slot number (1-4). StartTime and endTime will be automatically set based on the slot. " +
            "Slot 1: 08:00-11:00, Slot 2: 11:00-14:00, Slot 3: 14:00-17:00, Slot 4: 17:00-20:00", 
            example = "1", required = true)
    @Min(value = 1, message = "Slot number must be between 1 and 4")
    @Max(value = 4, message = "Slot number must be between 1 and 4")
    private Integer slotNumber;

    @NotNull(message = "Booking date is required")
    @Schema(description = "Booking date. Format: YYYY-MM-DD", 
            example = "2025-11-07", required = true)
    private String bookingDate;

    // Getters and Setters
    public Long getLabId() { return labId; }
    public void setLabId(Long labId) { this.labId = labId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getParticipantsCount() { return participantsCount; }
    public void setParticipantsCount(Integer participantsCount) { this.participantsCount = participantsCount; }

    public Integer getSlotNumber() { return slotNumber; }
    public void setSlotNumber(Integer slotNumber) { this.slotNumber = slotNumber; }

    public String getBookingDate() { return bookingDate; }
    public void setBookingDate(String bookingDate) { this.bookingDate = bookingDate; }
}
