package com.unilab.dto.attendance;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class AttendanceDto {
    // Getters and setters
    private Long id;
    private Long userId;
    private String userName;
    private Long bookingId;
    private String labName;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private Long totalDurationMinutes;

}
