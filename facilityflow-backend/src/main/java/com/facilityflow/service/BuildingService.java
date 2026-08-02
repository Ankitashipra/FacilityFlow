package com.facilityflow.service;

import com.facilityflow.dto.request.BuildingRequest;
import com.facilityflow.dto.response.BuildingResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BuildingService {
    BuildingResponse create(BuildingRequest request);
    BuildingResponse update(Long id, BuildingRequest request);
    BuildingResponse getById(Long id);
    Page<BuildingResponse> getAll(Pageable pageable);
    Page<BuildingResponse> search(String term, Pageable pageable);
    void delete(Long id);
}
