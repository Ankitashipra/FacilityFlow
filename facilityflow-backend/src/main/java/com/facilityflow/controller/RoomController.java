package com.facilityflow.controller;

import com.facilityflow.dto.request.RoomRequest;
import com.facilityflow.dto.response.ApiResponse;
import com.facilityflow.dto.response.PageResponse;
import com.facilityflow.dto.response.RoomResponse;
import com.facilityflow.entity.RoomStatus;
import com.facilityflow.service.RoomService;
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
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
@Tag(name = "Room Management", description = "Rooms, capacity, and status")
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','FACILITY_MANAGER')")
    @Operation(summary = "Create a room")
    public ResponseEntity<ApiResponse<RoomResponse>> create(@Valid @RequestBody RoomRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Room created", roomService.create(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','FACILITY_MANAGER')")
    @Operation(summary = "Update a room")
    public ResponseEntity<ApiResponse<RoomResponse>> update(@PathVariable Long id, @Valid @RequestBody RoomRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Room updated", roomService.update(id, request)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a room by id")
    public ResponseEntity<ApiResponse<RoomResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(roomService.getById(id)));
    }

    @GetMapping
    @Operation(summary = "List rooms (paginated, filter by floor/status, or search)")
    public ResponseEntity<ApiResponse<PageResponse<RoomResponse>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Long floorId,
            @RequestParam(required = false) RoomStatus status,
            @RequestParam(required = false) String search) {

        var pageable = PageUtils.of(page, size, "id", "ASC");

        var result = (search != null && !search.isBlank()) ? roomService.search(search, pageable)
                : floorId != null ? roomService.getByFloor(floorId, pageable)
                : status != null ? roomService.getByStatus(status, pageable)
                : roomService.getAll(pageable);

        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(result)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','FACILITY_MANAGER')")
    @Operation(summary = "Delete a room")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        roomService.delete(id);
        return ResponseEntity.ok(ApiResponse.message("Room deleted"));
    }
}
