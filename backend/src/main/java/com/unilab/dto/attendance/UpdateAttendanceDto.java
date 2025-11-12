package com.unilab.dto.attendance;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
public class UpdateAttendanceDto {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime checkInTime;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime checkOutTime;

    // --- Constructors ---
    public UpdateAttendanceDto() {
    }

    public UpdateAttendanceDto(LocalDateTime checkInTime, LocalDateTime checkOutTime) {
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
    }
}
