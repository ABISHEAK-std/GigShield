package com.allixia.repository;

import com.allixia.entity.InsurancePolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InsurancePolicyRepository extends JpaRepository<InsurancePolicy, UUID> {
    
    Optional<InsurancePolicy> findByPolicyNumber(String policyNumber);
    
    List<InsurancePolicy> findByUserId(UUID userId);
    
    @Query("SELECT p FROM InsurancePolicy p WHERE p.userId = :userId AND p.status = 'ACTIVE' AND p.startDate <= :now AND p.endDate >= :now")
    List<InsurancePolicy> findActivePoliciesByUserId(UUID userId, LocalDateTime now);
    
    List<InsurancePolicy> findByStatus(String status);
}
