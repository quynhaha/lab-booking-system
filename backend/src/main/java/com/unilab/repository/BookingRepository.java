package com.unilab.repository;

import com.unilab.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> findByBookingCode(String bookingCode);

    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId AND b.Id = :bookingId")
    Optional<Booking> findByBookingIdAndUserId(@Param("bookingId") long bookingId, @Param("userId") long userId);

    List<Booking> findByUserId(Long userId);

    List<Booking> findByLabId(Long labId);

    List<Booking> findByStatus(String status);

    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId AND b.status = :status")
    List<Booking> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") String status);

    @Query("SELECT b FROM Booking b WHERE b.lab.id = :labId " +
            "AND b.status NOT IN ('CANCELLED', 'REJECTED') " +
            "AND ((b.startTime BETWEEN :startTime AND :endTime) " +
            "OR (b.endTime BETWEEN :startTime AND :endTime) " +
            "OR (b.startTime <= :startTime AND b.endTime >= :endTime))")
    List<Booking> findConflictingBookings(
            @Param("labId") Long labId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    @Query("SELECT b FROM Booking b WHERE b.startTime >= :startDate AND b.endTime <= :endDate")
    List<Booking> findByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT b FROM Booking b WHERE b.startTime >= :startDate AND b.endTime <= :endDate " +
            "AND b.status = :status")
    List<Booking> findByDateRangeAndStatus(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("status") String status
    );

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.status = :status")
    Long countByStatus(@Param("status") String status);

    @Query("SELECT b FROM Booking b WHERE b.parentBookingId = :parentId")
    List<Booking> findChildBookings(@Param("parentId") Long parentId);

    @Query("SELECT b FROM Booking b WHERE b.refundStatus = 'PENDING' ORDER BY b.cancelledAt DESC")
    List<Booking> findPendingRefunds();

    @Query("SELECT b FROM Booking b WHERE b.status = 'PENDING' " +
            "AND b.category.requiresApproval = true ORDER BY b.createdAt ASC")
    List<Booking> findPendingApprovals();

    @Query("SELECT b FROM Booking b ORDER BY b.createdAt DESC")
    List<Booking> findRecentBookings(@Param("limit") int limit);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.startTime >= :startDate AND b.endTime <= :endDate")
    Long countByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT b.status, COUNT(b) FROM Booking b " +
            "WHERE b.startTime >= :startDate AND b.endTime <= :endDate " +
            "GROUP BY b.status")
    List<Object[]> getBookingStatsByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("""
    SELECT b FROM Booking b
    WHERE b.user.id = :userId
    ORDER BY\s
        CASE\s
            WHEN b.status = 'PENDING' THEN 1
            WHEN b.status = 'APPROVED' THEN 2
            WHEN b.status = 'REJECTED' THEN 3
            WHEN b.status = 'CANCELLED' THEN 4
            WHEN b.status = 'COMPLETED' THEN 5
            ELSE 6
        END,
        b.startTime DESC
""")
    List<Booking> findAllByStudentId(@Param("userId") Long userId);
    
    @Query("SELECT b FROM Booking b WHERE b.lab.id = :labId " +
            "AND ((b.startTime >= :startDate AND b.startTime <= :endDate) " +
            "OR (b.endTime >= :startDate AND b.endTime <= :endDate) " +
            "OR (b.startTime <= :startDate AND b.endTime >= :endDate)) " +
            "ORDER BY b.startTime ASC")
    List<Booking> findByLabIdAndDateRange(
            @Param("labId") Long labId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

}
