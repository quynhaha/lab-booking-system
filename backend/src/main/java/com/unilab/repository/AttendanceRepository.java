package com.unilab.repository;

import com.unilab.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    @Query("SELECT a FROM Attendance a WHERE a.user.id = :userId")
    List<Attendance> findByUserId(@Param("userId") Long userId);

    @Query("SELECT a FROM Attendance a WHERE a.booking.id = :bookingId")
    List<Attendance> findByBookingId(@Param("bookingId") Long bookingId);

    @Query("SELECT a FROM Attendance a WHERE a.booking.id = :bookingId AND a.user.id = :userId")
    Attendance findByUserIdAndBookingId(@Param("userId") Long userId, @Param("bookingId") Long bookingId);

    @Query("SELECT a FROM Attendance a WHERE a.checkInTime BETWEEN :start AND :end")
    List<Attendance> findByDateRange(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.checkInTime BETWEEN :start AND :end")
    Long countByDateRange(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("SELECT a.user.fullName, COUNT(a) FROM Attendance a " +
            "WHERE a.checkInTime BETWEEN :start AND :end " +
            "GROUP BY a.user.fullName")
    List<Object[]> getAttendanceStatsByUser(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("SELECT a.booking.lab.name, COUNT(a) FROM Attendance a " +
            "WHERE a.checkInTime BETWEEN :start AND :end " +
            "GROUP BY a.booking.lab.name")
    List<Object[]> getAttendanceStatsByLab(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
