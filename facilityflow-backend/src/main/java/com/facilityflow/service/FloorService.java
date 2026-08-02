package com.facilityflow.service;

import com.facilityflow.dto.request.FloorRequest;
import com.facilityflow.dto.response.FloorResponse;

import java.util.List;

public interface FloorService {
    FloorResponse create(FloorRequest request);
    List<FloorResponse> getByBuilding(Long buildingId);
    FloorResponse getById(Long id);
    void delete(Long id);
}
