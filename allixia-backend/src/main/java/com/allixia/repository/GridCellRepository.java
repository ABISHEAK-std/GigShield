package com.allixia.repository;

import com.allixia.entity.GridCell;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GridCellRepository extends JpaRepository<GridCell, UUID> {
    
    Optional<GridCell> findByCellId(String cellId);
}
