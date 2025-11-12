package com.unilab.repository;

import com.unilab.model.BookingRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRuleRepository extends JpaRepository<BookingRule, Long> {
    
    Optional<BookingRule> findByCode(String code);
    
    List<BookingRule> findByRuleType(String ruleType);
    
    List<BookingRule> findByIsActive(Boolean isActive);
    
    @Query("SELECT br FROM BookingRule br WHERE br.isActive = true ORDER BY br.priority ASC")
    List<BookingRule> findAllActiveOrderedByPriority();
    
    @Query("SELECT br FROM BookingRule br WHERE br.appliesTo = :appliesTo AND br.isActive = true")
    List<BookingRule> findActiveRulesByAppliesTo(@Param("appliesTo") String appliesTo);
    
    @Query("SELECT br FROM BookingRule br " +
           "WHERE (br.appliesTo = 'ALL' OR br.appliesTo = :userType) " +
           "AND br.isActive = true ORDER BY br.priority ASC")
    List<BookingRule> findApplicableRules(@Param("userType") String userType);
    
    @Query("SELECT br FROM BookingRule br " +
           "WHERE br.categoryId = :categoryId AND br.isActive = true")
    List<BookingRule> findByCategoryId(@Param("categoryId") Long categoryId);
}
