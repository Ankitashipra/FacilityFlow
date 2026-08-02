package com.facilityflow.service;

import com.facilityflow.dto.request.RoomRequest;
import com.facilityflow.dto.response.RoomResponse;
import com.facilityflow.entity.RoomStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoomService {
    RoomResponse create(RoomRequest request);
    RoomResponse update(Long id, RoomRequest request);
    RoomResponse getById(Long id);
    Page<RoomResponse> getAll(Pageable pageable);
    Page<RoomResponse> getByFloor(Long floorId, Pageable pageable);
    Page<RoomResponse> getByStatus(RoomStatus status, Pageable pageable);
    Page<RoomResponse> search(String term, Pageable pageable);
    void delete(Long id);
}
