package com.facilityflow.service.impl;

import com.facilityflow.audit.AuditService;
import com.facilityflow.dto.request.FloorRequest;
import com.facilityflow.dto.response.FloorResponse;
import com.facilityflow.entity.AuditAction;
import com.facilityflow.entity.Building;
import com.facilityflow.entity.Floor;
import com.facilityflow.exception.DuplicateResourceException;
import com.facilityflow.exception.ResourceNotFoundException;
import com.facilityflow.mapper.FloorMapper;
import com.facilityflow.repository.BuildingRepository;
import com.facilityflow.repository.FloorRepository;
import com.facilityflow.service.FloorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FloorServiceImpl implements FloorService {

    private final FloorRepository floorRepository;
    private final BuildingRepository buildingRepository;
    private final FloorMapper floorMapper;
    private final AuditService auditService;

    @Override
    public FloorResponse create(FloorRequest request) {
        Building building = buildingRepository.findById(request.getBuildingId())
                .orElseThrow(() -> ResourceNotFoundException.of("Building", request.getBuildingId()));

        if (floorRepository.existsByBuildingIdAndFloorNumber(building.getId(), request.getFloorNumber())) {
            throw new DuplicateResourceException("Floor " + request.getFloorNumber() + " already exists in this building");
        }

        Floor floor = Floor.builder()
                .building(building)
                .floorNumber(request.getFloorNumber())
                .name(request.getName())
                .build();
        floor = floorRepository.save(floor);
        auditService.record(AuditAction.CREATE, "Floor", floor.getId(), "Floor created in building " + building.getName());
        return floorMapper.toResponse(floor);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FloorResponse> getByBuilding(Long buildingId) {
        return floorRepository.findByBuildingId(buildingId).stream()
                .map(floorMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public FloorResponse getById(Long id) {
        return floorMapper.toResponse(findOrThrow(id));
    }

    @Override
    public void delete(Long id) {
        Floor floor = findOrThrow(id);
        floor.markDeleted();
        floorRepository.save(floor);
        auditService.record(AuditAction.DELETE, "Floor", id, "Floor soft-deleted");
    }

    private Floor findOrThrow(Long id) {
        return floorRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Floor", id));
    }
}
