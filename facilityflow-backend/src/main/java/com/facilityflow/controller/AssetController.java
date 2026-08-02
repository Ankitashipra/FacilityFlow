package com.facilityflow.controller;

import com.facilityflow.dto.request.AssetRequest;
import com.facilityflow.dto.response.ApiResponse;
import com.facilityflow.dto.response.AssetResponse;
import com.facilityflow.dto.response.PageResponse;
import com.facilityflow.entity.AssetStatus;
import com.facilityflow.service.AssetService;
import com.facilityflow.util.PageUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/assets")
@RequiredArgsConstructor
@Tag(name = "Asset Management", description = "Computers, chairs, ACs, projectors, printers, and their QR codes")
public class AssetController {

    private final AssetService assetService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','FACILITY_MANAGER')")
    @Operation(summary = "Register a new asset (auto-generates a QR code)")
    public ResponseEntity<ApiResponse<AssetResponse>> create(@Valid @RequestBody AssetRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Asset registered", assetService.create(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','FACILITY_MANAGER')")
    @Operation(summary = "Update an asset")
    public ResponseEntity<ApiResponse<AssetResponse>> update(@PathVariable Long id, @Valid @RequestBody AssetRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Asset updated", assetService.update(id, request)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an asset by id")
    public ResponseEntity<ApiResponse<AssetResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(assetService.getById(id)));
    }

    @GetMapping
    @Operation(summary = "List assets (paginated, filter by room/status, or search)")
    public ResponseEntity<ApiResponse<PageResponse<AssetResponse>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Long roomId,
            @RequestParam(required = false) AssetStatus status,
            @RequestParam(required = false) String search) {

        var pageable = PageUtils.of(page, size, "id", "ASC");

        var result = (search != null && !search.isBlank()) ? assetService.search(search, pageable)
                : roomId != null ? assetService.getByRoom(roomId, pageable)
                : status != null ? assetService.getByStatus(status, pageable)
                : assetService.getAll(pageable);

        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(result)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','FACILITY_MANAGER')")
    @Operation(summary = "Delete an asset")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        assetService.delete(id);
        return ResponseEntity.ok(ApiResponse.message("Asset deleted"));
    }
}
