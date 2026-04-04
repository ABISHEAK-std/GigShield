package com.allixia.repository;

import com.allixia.entity.WorkerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkerStatusRepository extends JpaRepository<WorkerStatus, UUID> {
    
    Optional<WorkerStatus> findByUserId(UUID userId);
}
