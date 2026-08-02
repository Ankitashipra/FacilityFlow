package com.facilityflow.service;

import com.facilityflow.dto.request.AssetRequest;
import com.facilityflow.dto.response.AssetResponse;
import com.facilityflow.entity.AssetStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AssetService {
    AssetResponse create(AssetRequest request);
    AssetResponse update(Long id, AssetRequest request);
    AssetResponse getById(Long id);
    Page<AssetResponse> getAll(Pageable pageable);
    Page<AssetResponse> getByRoom(Long roomId, Pageable pageable);
    Page<AssetResponse> getByStatus(AssetStatus status, Pageable pageable);
    Page<AssetResponse> search(String term, Pageable pageable);
    void delete(Long id);
}
