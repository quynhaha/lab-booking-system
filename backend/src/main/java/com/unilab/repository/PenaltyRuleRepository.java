package com.unilab.repository;

import com.unilab.model.PenaltyRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PenaltyRuleRepository extends JpaRepository<PenaltyRule, Long> {
    
    Optional<PenaltyRule> findByCode(String code);
    
    List<PenaltyRule> findByViolationType(String violationType);
    
    List<PenaltyRule> findByIsActive(Boolean isActive);
    
    @Query("SELECT pr FROM PenaltyRule pr WHERE pr.isActive = true ORDER BY pr.name")
    List<PenaltyRule> findAllActive();
    
    @Query("SELECT pr FROM PenaltyRule pr WHERE pr.violationType = :violationType AND pr.isActive = true")
    Optional<PenaltyRule> findActiveRuleByViolationType(@Param("violationType") String violationType);
}
