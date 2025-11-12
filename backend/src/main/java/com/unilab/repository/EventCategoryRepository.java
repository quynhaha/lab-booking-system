package com.unilab.repository;

import com.unilab.model.EventCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventCategoryRepository extends JpaRepository<EventCategory, Long> {
    
    Optional<EventCategory> findByCode(String code);
    
    List<EventCategory> findByIsActive(Boolean isActive);
    
    @Query("SELECT ec FROM EventCategory ec WHERE ec.isActive = true ORDER BY ec.name")
    List<EventCategory> findAllActive();
    
    @Query("SELECT COUNT(ec) FROM EventCategory ec WHERE ec.isActive = true")
    Long countActive();
}
