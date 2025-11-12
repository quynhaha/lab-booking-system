package com.unilab.service;

import com.unilab.dto.attendance.AttendanceDto;
import com.unilab.dto.attendance.CheckAttendanceDto;
import com.unilab.dto.attendance.CreateAttendanceDto;
import com.unilab.dto.attendance.UpdateAttendanceDto;
import com.unilab.model.Attendance;
import com.unilab.model.Booking;
import com.unilab.model.User;
import com.unilab.repository.AttendanceRepository;
import com.unilab.repository.BookingRepository;
import com.unilab.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    // Get all attendance records
    public List<AttendanceDto> getAllAttendances() {
        return attendanceRepository.findAll().stream().map(this::convertToDto).collect(Collectors.toList());
    }

    // Get attendance by user
    public List<AttendanceDto> getAttendancesByUser(Long userId) {
        return attendanceRepository.findByUserId(userId).stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public AttendanceDto getAttendanceById(Long attendanceId) {

        var attendance = attendanceRepository.findById(attendanceId).orElseThrow(() -> new RuntimeException("Attendance not found with id: " + attendanceId));
        return convertToDto(attendance);
    }

    // Get attendance by user and booking
    public AttendanceDto getAttendancesByUserAndBooking(Long userId, long bookingId) {

        var attendance = attendanceRepository.findByUserIdAndBookingId(userId, bookingId);

        if (attendance == null) {
            throw new RuntimeException("Attendance not found");
        }

        return convertToDto(attendance);
    }

    // Get attendance by booking
    public List<AttendanceDto> getAttendancesByBooking(Long bookingId) {
        return attendanceRepository.findByBookingId(bookingId).stream().map(this::convertToDto).collect(Collectors.toList());
    }

    // Get attendance by date range
    public List<AttendanceDto> getAttendancesByDateRange(LocalDateTime start, LocalDateTime end) {
        return attendanceRepository.findByDateRange(start, end).stream().map(this::convertToDto).collect(Collectors.toList());
    }

    // Count total attendance within range
    public Long countAttendances(LocalDateTime start, LocalDateTime end) {
        return attendanceRepository.countByDateRange(start, end);
    }

    // Statistics: attendance count per user
    public List<Object[]> getStatsByUser(LocalDateTime start, LocalDateTime end) {
        return attendanceRepository.getAttendanceStatsByUser(start, end);
    }

    // Statistics: attendance count per lab
    public List<Object[]> getStatsByLab(LocalDateTime start, LocalDateTime end) {
        return attendanceRepository.getAttendanceStatsByLab(start, end);
    }

    // Create or update attendance record
    public AttendanceDto createAttendance(CreateAttendanceDto createAttendanceDto) {

        User user = userRepository.findById(createAttendanceDto.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));

        Booking booking = bookingRepository.findByBookingIdAndUserId(createAttendanceDto.getBookingId(), createAttendanceDto.getUserId()).orElseThrow(() -> new RuntimeException("Booking not found with this booking ID and user ID"));

        Attendance attendance = attendanceRepository.findByUserIdAndBookingId(createAttendanceDto.getUserId(), createAttendanceDto.getBookingId());

        if (attendance != null) {
            throw new IllegalArgumentException("Attendance already exists.");
        }

        // Valid check in time and check out time
        if (createAttendanceDto.getCheckInTime() != null && createAttendanceDto.getCheckOutTime() != null && createAttendanceDto.getCheckOutTime().isBefore(createAttendanceDto.getCheckInTime())) {
            throw new IllegalArgumentException("Check-out time cannot be earlier than check-in time.");
        }

        attendance = new Attendance();
        attendance.setUser(user);
        attendance.setBooking(booking);
        attendance.setCheckInTime(createAttendanceDto.getCheckInTime());
        attendance.setCheckOutTime(createAttendanceDto.getCheckOutTime());

        Attendance saved = attendanceRepository.save(attendance);
        return convertToDto(saved);
    }

    public AttendanceDto updateAttendance(Long attendanceId, UpdateAttendanceDto dto) {

        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new RuntimeException("Attendance not found with ID: " + attendanceId));

        LocalDateTime checkIn = dto.getCheckInTime();
        LocalDateTime checkOut = dto.getCheckOutTime();

        if (checkIn != null && checkOut != null && checkOut.isBefore(checkIn)) {
            throw new IllegalArgumentException("Check-out time cannot be earlier than check-in time.");
        }

        if (checkIn != null) attendance.setCheckInTime(checkIn);
        if (checkOut != null) attendance.setCheckOutTime(checkOut);

        Attendance saved = attendanceRepository.save(attendance);

        return convertToDto(saved);
    }

    public AttendanceDto deleteAttendance(Long attendanceId) {

        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new RuntimeException("Attendance not found with ID: " + attendanceId));

        attendanceRepository.delete(attendance);

        return convertToDto(attendance);
    }

    // Check in and check out
    public AttendanceDto checkAttendance(CheckAttendanceDto dto) {

        // Validate user & booking
        User user = userRepository.findById(dto.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));

        Booking booking = bookingRepository.findByBookingIdAndUserId(dto.getBookingId(), dto.getUserId()).orElseThrow(() -> new RuntimeException("Booking not found with this booking ID and user ID"));

        // Validate booking status
        if (!"APPROVED".equalsIgnoreCase(booking.getStatus())) {
            throw new IllegalStateException("Cannot check in or out for an unapproved booking.");
        }

        // Get current time once
        LocalDateTime now = LocalDateTime.now();

        // Find or create attendance record
        Attendance attendance = attendanceRepository.findByUserIdAndBookingId(dto.getUserId(), dto.getBookingId());
        if (attendance == null) {
            attendance = new Attendance();
            attendance.setUser(user);
            attendance.setBooking(booking);
        }

        // Handle check-in
        if (attendance.getCheckInTime() == null) {

            if (booking.getStartTime().isAfter(now)) {
                throw new IllegalStateException("Cannot check in before the booking start time.");
            }

            if (booking.getEndTime().isBefore(now)) {
                throw new IllegalStateException("Cannot check in after the booking has ended.");
            }

            attendance.setCheckInTime(now);
        }
        // Handle check-out
        else if (attendance.getCheckOutTime() == null) {

            if (booking.getEndTime().isAfter(now)) {
                throw new IllegalStateException("Cannot check out before the booking end time.");
            }

            attendance.setCheckOutTime(now);

        }
        //  Already checked out
        else {
            throw new IllegalStateException("User has already checked out.");
        }

        Attendance saved = attendanceRepository.save(attendance);
        return convertToDto(saved);
    }

    // Convert Entity → DTO
    private AttendanceDto convertToDto(Attendance attendance) {
        AttendanceDto dto = new AttendanceDto();
        dto.setId(attendance.getId());
        dto.setUserId(attendance.getUser().getId());
        dto.setUserName(attendance.getUser().getFullName());
        dto.setBookingId(attendance.getBooking().getId());
        dto.setLabName(attendance.getBooking().getLab().getName());
        dto.setCheckInTime(attendance.getCheckInTime());
        dto.setCheckOutTime(attendance.getCheckOutTime());

        if (attendance.getCheckInTime() != null && attendance.getCheckOutTime() != null) {
            long minutes = Duration.between(attendance.getCheckInTime(), attendance.getCheckOutTime()).toMinutes();
            dto.setTotalDurationMinutes(minutes);
        } else {
            dto.setTotalDurationMinutes(0L);
        }

        return dto;
    }
}
