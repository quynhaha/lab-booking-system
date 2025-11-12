package com.unilab.repository;

import com.unilab.model.BookingLimit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingLimitRepository extends JpaRepository<BookingLimit, Long> {
    
    // Find active booking limits
    List<BookingLimit> findByIsActiveTrue();
    
    // Find booking limit by name
    Optional<BookingLimit> findByName(String name);
    
    // Find booking limit by name and active status
    Optional<BookingLimit> findByNameAndIsActiveTrue(String name);
}
