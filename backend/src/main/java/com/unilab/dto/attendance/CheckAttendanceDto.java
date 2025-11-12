package com.unilab.dto.attendance;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CheckAttendanceDto {
    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Booking ID is required")
    private Long bookingId;
}
