package com.facilityflow.service.impl;

import com.facilityflow.audit.AuditService;
import com.facilityflow.dto.request.BuildingRequest;
import com.facilityflow.dto.response.BuildingResponse;
import com.facilityflow.entity.AuditAction;
import com.facilityflow.entity.Building;
import com.facilityflow.exception.DuplicateResourceException;
import com.facilityflow.exception.ResourceNotFoundException;
import com.facilityflow.mapper.BuildingMapper;
import com.facilityflow.repository.BuildingRepository;
import com.facilityflow.service.BuildingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BuildingServiceImpl implements BuildingService {

    private final BuildingRepository buildingRepository;
    private final BuildingMapper buildingMapper;
    private final AuditService auditService;

    @Override
    public BuildingResponse create(BuildingRequest request) {
        if (buildingRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("A building with code '" + request.getCode() + "' already exists");
        }
        Building building = Building.builder()
                .name(request.getName())
                .code(request.getCode())
                .address(request.getAddress())
                .city(request.getCity())
                .totalFloors(request.getTotalFloors())
                .build();
        building = buildingRepository.save(building);
        auditService.record(AuditAction.CREATE, "Building", building.getId(), "Building created: " + building.getName());
        return buildingMapper.toResponse(building);
    }

    @Override
    public BuildingResponse update(Long id, BuildingRequest request) {
        Building building = findOrThrow(id);
        building.setName(request.getName());
        building.setAddress(request.getAddress());
        building.setCity(request.getCity());
        building.setTotalFloors(request.getTotalFloors());
        building = buildingRepository.save(building);
        auditService.record(AuditAction.UPDATE, "Building", building.getId(), "Building updated");
        return buildingMapper.toResponse(building);
    }

    @Override
    @Transactional(readOnly = true)
    public BuildingResponse getById(Long id) {
        return buildingMapper.toResponse(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BuildingResponse> getAll(Pageable pageable) {
        return buildingRepository.findAll(pageable).map(buildingMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BuildingResponse> search(String term, Pageable pageable) {
        return buildingRepository.findByNameContainingIgnoreCase(term, pageable).map(buildingMapper::toResponse);
    }

    @Override
    public void delete(Long id) {
        Building building = findOrThrow(id);
        building.markDeleted();
        buildingRepository.save(building);
        auditService.record(AuditAction.DELETE, "Building", id, "Building soft-deleted");
    }

    private Building findOrThrow(Long id) {
        return buildingRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Building", id));
    }
}
