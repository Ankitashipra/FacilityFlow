package com.facilityflow.mapper;

import com.facilityflow.dto.response.RoomResponse;
import com.facilityflow.entity.Room;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoomMapper {

    @Mapping(target = "floorId", source = "floor.id")
    @Mapping(target = "floorName", source = "floor.name")
    @Mapping(target = "buildingId", source = "floor.building.id")
    @Mapping(target = "buildingName", source = "floor.building.name")
    @Mapping(target = "assetCount", expression = "java(room.getAssets() == null ? 0 : room.getAssets().size())")
    RoomResponse toResponse(Room room);
}
