package com.unilab.repository;

import com.unilab.model.Lab;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LabRepository extends JpaRepository<Lab, Long> {
    
    // Optional<Lab> findByLabCode(String labCode); // labCode field removed from Lab entity
    
    List<Lab> findByStatus(String status);
    
    @Query("SELECT l FROM Lab l WHERE l.status = 'AVAILABLE' ORDER BY l.name")
    List<Lab> findAllAvailableLabs();
    
    @Query("SELECT l FROM Lab l WHERE l.name LIKE %:keyword% OR l.location LIKE %:keyword%")
    List<Lab> searchLabs(@Param("keyword") String keyword);
    
    @Query("SELECT COUNT(l) FROM Lab l WHERE l.status = :status")
    Long countByStatus(@Param("status") String status);
}
