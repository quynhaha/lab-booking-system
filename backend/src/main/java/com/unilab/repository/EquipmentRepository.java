package com.unilab.repository;

import com.unilab.model.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    
    // Find equipment by name (case-insensitive)
    List<Equipment> findByNameContainingIgnoreCase(String name);
    
    // Find equipment by type
    List<Equipment> findByType(String type);
    
    // Find equipment by status
    List<Equipment> findByStatus(String status);
    
    // Find equipment by lab
    List<Equipment> findByLabId(Long labId);
    
    // Search equipment by name or type (case-insensitive)
    @Query("SELECT e FROM Equipment e WHERE " +
           "LOWER(e.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(e.type) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Equipment> searchByNameOrType(@Param("searchTerm") String searchTerm);
    
    // Find available equipment
    List<Equipment> findByStatusAndLabId(String status, Long labId);
}
