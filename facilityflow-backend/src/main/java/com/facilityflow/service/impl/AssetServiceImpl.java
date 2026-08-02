package com.facilityflow.service.impl;

import com.facilityflow.audit.AuditService;
import com.facilityflow.dto.request.AssetRequest;
import com.facilityflow.dto.response.AssetResponse;
import com.facilityflow.entity.Asset;
import com.facilityflow.entity.AssetStatus;
import com.facilityflow.entity.AuditAction;
import com.facilityflow.entity.Room;
import com.facilityflow.exception.DuplicateResourceException;
import com.facilityflow.exception.ResourceNotFoundException;
import com.facilityflow.mapper.AssetMapper;
import com.facilityflow.repository.AssetRepository;
import com.facilityflow.repository.RoomRepository;
import com.facilityflow.service.AssetService;
import com.facilityflow.util.QrCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AssetServiceImpl implements AssetService {

    private final AssetRepository assetRepository;
    private final RoomRepository roomRepository;
    private final AssetMapper assetMapper;
    private final QrCodeGenerator qrCodeGenerator;
    private final AuditService auditService;

    @Override
    public AssetResponse create(AssetRequest request) {
        if (assetRepository.existsByAssetTag(request.getAssetTag())) {
            throw new DuplicateResourceException("An asset with tag '" + request.getAssetTag() + "' already exists");
        }

        Room room = null;
        if (request.getRoomId() != null) {
            room = roomRepository.findById(request.getRoomId())
                    .orElseThrow(() -> ResourceNotFoundException.of("Room", request.getRoomId()));
        }

        Asset asset = Asset.builder()
                .assetTag(request.getAssetTag())
                .name(request.getName())
                .type(request.getType())
                .status(request.getStatus() != null ? request.getStatus() : AssetStatus.ACTIVE)
                .room(room)
                .purchaseDate(request.getPurchaseDate())
                .warrantyExpiryDate(request.getWarrantyExpiryDate())
                .serialNumber(request.getSerialNumber())
                .vendor(request.getVendor())
                .purchaseCost(request.getPurchaseCost())
                .build();

        asset.setQrCodeUrl(qrCodeGenerator.generateBase64(request.getAssetTag()));

        asset = assetRepository.save(asset);
        auditService.record(AuditAction.CREATE, "Asset", asset.getId(), "Asset registered: " + asset.getAssetTag());
        return assetMapper.toResponse(asset);
    }

    @Override
    public AssetResponse update(Long id, AssetRequest request) {
        Asset asset = findOrThrow(id);

        if (request.getRoomId() != null) {
            Room room = roomRepository.findById(request.getRoomId())
                    .orElseThrow(() -> ResourceNotFoundException.of("Room", request.getRoomId()));
            asset.setRoom(room);
        }

        asset.setName(request.getName());
        asset.setType(request.getType());
        if (request.getStatus() != null) asset.setStatus(request.getStatus());
        asset.setPurchaseDate(request.getPurchaseDate());
        asset.setWarrantyExpiryDate(request.getWarrantyExpiryDate());
        asset.setSerialNumber(request.getSerialNumber());
        asset.setVendor(request.getVendor());
        asset.setPurchaseCost(request.getPurchaseCost());

        asset = assetRepository.save(asset);
        auditService.record(AuditAction.UPDATE, "Asset", asset.getId(), "Asset updated");
        return assetMapper.toResponse(asset);
    }

    @Override
    @Transactional(readOnly = true)
    public AssetResponse getById(Long id) {
        return assetMapper.toResponse(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AssetResponse> getAll(Pageable pageable) {
        return assetRepository.findAll(pageable).map(assetMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AssetResponse> getByRoom(Long roomId, Pageable pageable) {
        return assetRepository.findByRoomId(roomId, pageable).map(assetMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AssetResponse> getByStatus(AssetStatus status, Pageable pageable) {
        return assetRepository.findByStatus(status, pageable).map(assetMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AssetResponse> search(String term, Pageable pageable) {
        return assetRepository.search(term, pageable).map(assetMapper::toResponse);
    }

    @Override
    public void delete(Long id) {
        Asset asset = findOrThrow(id);
        asset.markDeleted();
        assetRepository.save(asset);
        auditService.record(AuditAction.DELETE, "Asset", id, "Asset soft-deleted");
    }

    private Asset findOrThrow(Long id) {
        return assetRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Asset", id));
    }
}
