package com.unilab.api;

import com.unilab.dto.attendance.AttendanceDto;
import com.unilab.dto.attendance.CheckAttendanceDto;
import com.unilab.dto.attendance.CreateAttendanceDto;
import com.unilab.dto.attendance.UpdateAttendanceDto;
import com.unilab.repository.UserRepository;
import com.unilab.service.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/attendance")
@Tag(name = "Attendance", description = "Attendance report and tracking endpoints")
@SecurityRequirement(name = "bearerAuth")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private UserRepository userRepository;

    // Get all attendance records
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Get all attendance records", description = "Retrieve all attendance data")
    public ResponseEntity<List<AttendanceDto>> getAllAttendance() {
        return ResponseEntity.ok(attendanceService.getAllAttendances());
    }

    // Get attendance by id
    @GetMapping("/{attendanceId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Get attendance by booking", description = "Retrieve all attendance records for a specific booking")
    public ResponseEntity<AttendanceDto> getAttendanceById(@Parameter(name = "attendanceId", description = "ID of the booking", required = true) @PathVariable("attendanceId") Long attendanceId) {
        return ResponseEntity.ok(attendanceService.getAttendanceById(attendanceId));
    }

    // Create attendance (manual or system update)
    @PostMapping("/")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Create attendance record (create manually if mistake occur)", description = "Create attendance record for a booking and user")
    public ResponseEntity<AttendanceDto> createAttendance(@Valid @RequestBody CreateAttendanceDto createAttendanceDto) {
        return ResponseEntity.ok(attendanceService.createAttendance(createAttendanceDto));
    }

    // Create attendance (manual or system update)
    @PutMapping("/{attendanceId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Update attendance record", description = "Update attendance record for a booking and user")
    public ResponseEntity<AttendanceDto> updateAttendance(@Parameter(name = "attendanceId", description = "ID of the attendance", required = true) @PathVariable("attendanceId") Long attendanceId, @Valid @RequestBody UpdateAttendanceDto updateAttendanceDto) {
        return ResponseEntity.ok(attendanceService.updateAttendance(attendanceId, updateAttendanceDto));
    }

    // Create attendance (manual or system update)
    @DeleteMapping("/{attendanceId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Delete attendance record", description = "Delete attendance record for a booking and user")
    public ResponseEntity<AttendanceDto> deleteAttendance(@Parameter(name = "attendanceId", description = "ID of the attendance", required = true) @PathVariable("attendanceId") Long attendanceId) {
        return ResponseEntity.ok(attendanceService.deleteAttendance(attendanceId));
    }

    // Check in / out attendance
    @PostMapping("/check")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'TEACHER', 'STUDENT')")
    @Operation(summary = "Check an attendance record both for check in and check out", description = "Check an attendance record for a booking and user")
    public ResponseEntity<AttendanceDto> checkAttendance(@Valid @RequestBody CheckAttendanceDto checkAttendanceDto) {
        return ResponseEntity.ok(attendanceService.checkAttendance(checkAttendanceDto));
    }

    // Self-view attendance (for logged in user) base booking id
    @GetMapping("/booking/{bookingId}/me")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'TEACHER', 'STUDENT')")
    @Operation(summary = "Get my attendance records", description = "Retrieve attendance records for the logged-in user and selected booking")
    public ResponseEntity<AttendanceDto> getMyAttendance(Authentication authentication, @Parameter(name = "bookingId", description = "ID of the booking", required = true) @PathVariable("bookingId") Long bookingId) {
        String email = authentication.getName();
        Long userId = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found")).getId();

        return ResponseEntity.ok(attendanceService.getAttendancesByUserAndBooking(userId, bookingId));
    }
}
