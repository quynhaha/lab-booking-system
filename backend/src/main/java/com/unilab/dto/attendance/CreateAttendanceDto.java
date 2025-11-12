package com.unilab.dto.attendance;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Setter
@Getter
public class CreateAttendanceDto {
    // --- Getters & Setters ---
    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Booking ID is required")
    private Long bookingId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime checkInTime;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime checkOutTime;

    // --- Constructors ---
    public CreateAttendanceDto() {
    }

    public CreateAttendanceDto(Long userId, Long bookingId, LocalDateTime checkInTime, LocalDateTime checkOutTime) {
        this.userId = userId;
        this.bookingId = bookingId;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
    }

}
