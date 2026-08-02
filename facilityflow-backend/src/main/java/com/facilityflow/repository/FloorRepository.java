package com.facilityflow.repository;

import com.facilityflow.entity.Floor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FloorRepository extends JpaRepository<Floor, Long> {
    List<Floor> findByBuildingId(Long buildingId);
    boolean existsByBuildingIdAndFloorNumber(Long buildingId, Integer floorNumber);
}
