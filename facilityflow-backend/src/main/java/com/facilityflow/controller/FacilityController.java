package com.facilityflow.controller;

import com.facilityflow.dto.request.BuildingRequest;
import com.facilityflow.dto.request.FloorRequest;
import com.facilityflow.dto.response.ApiResponse;
import com.facilityflow.dto.response.BuildingResponse;
import com.facilityflow.dto.response.FloorResponse;
import com.facilityflow.dto.response.PageResponse;
import com.facilityflow.service.BuildingService;
import com.facilityflow.service.FloorService;
import com.facilityflow.util.PageUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/facilities")
@RequiredArgsConstructor
@Tag(name = "Facility Management", description = "Buildings and floors")
public class FacilityController {

    private final BuildingService buildingService;
    private final FloorService floorService;

    // ---- Buildings ----

    @PostMapping("/buildings")
    @PreAuthorize("hasAnyRole('ADMIN','FACILITY_MANAGER')")
    @Operation(summary = "Create a building")
    public ResponseEntity<ApiResponse<BuildingResponse>> createBuilding(@Valid @RequestBody BuildingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Building created", buildingService.create(request)));
    }

    @PutMapping("/buildings/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','FACILITY_MANAGER')")
    @Operation(summary = "Update a building")
    public ResponseEntity<ApiResponse<BuildingResponse>> updateBuilding(@PathVariable Long id,
                                                                          @Valid @RequestBody BuildingRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Building updated", buildingService.update(id, request)));
    }

    @GetMapping("/buildings/{id}")
    @Operation(summary = "Get a building by id")
    public ResponseEntity<ApiResponse<BuildingResponse>> getBuilding(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(buildingService.getById(id)));
    }

    @GetMapping("/buildings")
    @Operation(summary = "List buildings (paginated, optional name search)")
    public ResponseEntity<ApiResponse<PageResponse<BuildingResponse>>> listBuildings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search) {
        var pageable = PageUtils.of(page, size, "id", "ASC");
        var result = (search != null && !search.isBlank())
                ? buildingService.search(search, pageable)
                : buildingService.getAll(pageable);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(result)));
    }

    @DeleteMapping("/buildings/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a building")
    public ResponseEntity<ApiResponse<Void>> deleteBuilding(@PathVariable Long id) {
        buildingService.delete(id);
        return ResponseEntity.ok(ApiResponse.message("Building deleted"));
    }

    // ---- Floors ----

    @PostMapping("/floors")
    @PreAuthorize("hasAnyRole('ADMIN','FACILITY_MANAGER')")
    @Operation(summary = "Create a floor within a building")
    public ResponseEntity<ApiResponse<FloorResponse>> createFloor(@Valid @RequestBody FloorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Floor created", floorService.create(request)));
    }

    @GetMapping("/buildings/{buildingId}/floors")
    @Operation(summary = "List floors for a building")
    public ResponseEntity<ApiResponse<List<FloorResponse>>> listFloors(@PathVariable Long buildingId) {
        return ResponseEntity.ok(ApiResponse.success(floorService.getByBuilding(buildingId)));
    }

    @GetMapping("/floors/{id}")
    @Operation(summary = "Get a floor by id")
    public ResponseEntity<ApiResponse<FloorResponse>> getFloor(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(floorService.getById(id)));
    }

    @DeleteMapping("/floors/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a floor")
    public ResponseEntity<ApiResponse<Void>> deleteFloor(@PathVariable Long id) {
        floorService.delete(id);
        return ResponseEntity.ok(ApiResponse.message("Floor deleted"));
    }
}
