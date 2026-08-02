package com.facilityflow.service.impl;

import com.facilityflow.audit.AuditService;
import com.facilityflow.dto.request.RoomRequest;
import com.facilityflow.dto.response.RoomResponse;
import com.facilityflow.entity.AuditAction;
import com.facilityflow.entity.Floor;
import com.facilityflow.entity.Room;
import com.facilityflow.entity.RoomStatus;
import com.facilityflow.exception.DuplicateResourceException;
import com.facilityflow.exception.ResourceNotFoundException;
import com.facilityflow.mapper.RoomMapper;
import com.facilityflow.repository.FloorRepository;
import com.facilityflow.repository.RoomRepository;
import com.facilityflow.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final FloorRepository floorRepository;
    private final RoomMapper roomMapper;
    private final AuditService auditService;

    @Override
    @CacheEvict(value = "rooms", allEntries = true)
    public RoomResponse create(RoomRequest request) {
        Floor floor = floorRepository.findById(request.getFloorId())
                .orElseThrow(() -> ResourceNotFoundException.of("Floor", request.getFloorId()));

        roomRepository.findByCode(request.getCode()).ifPresent(r -> {
            throw new DuplicateResourceException("A room with code '" + request.getCode() + "' already exists");
        });

        Room room = Room.builder()
                .floor(floor)
                .name(request.getName())
                .code(request.getCode())
                .type(request.getType())
                .status(request.getStatus() != null ? request.getStatus() : RoomStatus.AVAILABLE)
                .capacity(request.getCapacity())
                .build();
        room = roomRepository.save(room);
        auditService.record(AuditAction.CREATE, "Room", room.getId(), "Room created: " + room.getName());
        return roomMapper.toResponse(room);
    }

    @Override
    @CacheEvict(value = "rooms", allEntries = true)
    public RoomResponse update(Long id, RoomRequest request) {
        Room room = findOrThrow(id);

        if (request.getFloorId() != null && !request.getFloorId().equals(room.getFloor().getId())) {
            Floor floor = floorRepository.findById(request.getFloorId())
                    .orElseThrow(() -> ResourceNotFoundException.of("Floor", request.getFloorId()));
            room.setFloor(floor);
        }

        room.setName(request.getName());
        room.setType(request.getType());
        if (request.getStatus() != null) room.setStatus(request.getStatus());
        room.setCapacity(request.getCapacity());

        room = roomRepository.save(room);
        auditService.record(AuditAction.UPDATE, "Room", room.getId(), "Room updated");
        return roomMapper.toResponse(room);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "rooms", key = "#id")
    public RoomResponse getById(Long id) {
        return roomMapper.toResponse(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RoomResponse> getAll(Pageable pageable) {
        return roomRepository.findAll(pageable).map(roomMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RoomResponse> getByFloor(Long floorId, Pageable pageable) {
        return roomRepository.findByFloorId(floorId, pageable).map(roomMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RoomResponse> getByStatus(RoomStatus status, Pageable pageable) {
        return roomRepository.findByStatus(status, pageable).map(roomMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RoomResponse> search(String term, Pageable pageable) {
        return roomRepository.search(term, pageable).map(roomMapper::toResponse);
    }

    @Override
    @CacheEvict(value = "rooms", allEntries = true)
    public void delete(Long id) {
        Room room = findOrThrow(id);
        room.markDeleted();
        roomRepository.save(room);
        auditService.record(AuditAction.DELETE, "Room", id, "Room soft-deleted");
    }

    private Room findOrThrow(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Room", id));
    }
}
