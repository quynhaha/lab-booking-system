package com.unilab.repository;

import com.unilab.model.PenaltyHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PenaltyHistoryRepository extends JpaRepository<PenaltyHistory, Long> {
    
    List<PenaltyHistory> findByUserId(Long userId);
    
    List<PenaltyHistory> findByBookingId(Long bookingId);
    
    List<PenaltyHistory> findByStatus(String status);
    
    @Query("SELECT ph FROM PenaltyHistory ph WHERE ph.user.id = :userId AND ph.status = :status")
    List<PenaltyHistory> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") String status);
    
    @Query("SELECT ph FROM PenaltyHistory ph WHERE ph.user.id = :userId ORDER BY ph.createdAt DESC")
    List<PenaltyHistory> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);
    
    @Query("SELECT ph FROM PenaltyHistory ph " +
           "WHERE ph.createdAt >= :startDate AND ph.createdAt <= :endDate " +
           "ORDER BY ph.createdAt DESC")
    List<PenaltyHistory> findByDateRange(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    @Query("SELECT ph FROM PenaltyHistory ph ORDER BY ph.createdAt DESC")
    List<PenaltyHistory> findRecentPenalties(@Param("limit") int limit);
    
    @Query("SELECT COUNT(ph) FROM PenaltyHistory ph WHERE ph.status = :status")
    Long countByStatus(@Param("status") String status);
    
    @Query("SELECT SUM(ph.amount) FROM PenaltyHistory ph WHERE ph.status = :status")
    BigDecimal sumAmountByStatus(@Param("status") String status);
    
    @Query("SELECT ph.penaltyRule.violationType, COUNT(ph) FROM PenaltyHistory ph " +
           "WHERE ph.createdAt >= :startDate AND ph.createdAt <= :endDate " +
           "GROUP BY ph.penaltyRule.violationType")
    List<Object[]> getPenaltyStatsByTypeAndDateRange(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    @Query("SELECT SUM(ph.penaltyPoints) FROM PenaltyHistory ph " +
           "WHERE ph.user.id = :userId AND ph.status = 'PENDING'")
    Integer getTotalPenaltyPointsByUserId(@Param("userId") Long userId);
}
