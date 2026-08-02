package com.facilityflow.repository;

import com.facilityflow.entity.Building;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BuildingRepository extends JpaRepository<Building, Long> {
    Optional<Building> findByCode(String code);
    boolean existsByCode(String code);
    Page<Building> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
