package com.unilab.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "attendances")
public class Attendance {
    // Getters & Setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendance_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;

    @Column(name = "check_out_time")
    private LocalDateTime checkOutTime;

    // Constructors
    public Attendance() {
    }

    public Attendance(Booking booking, User user, LocalDateTime checkInTime, LocalDateTime checkOutTime) {
        this.booking = booking;
        this.user = user;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
    }

}
