package com.facilityflow.mapper;

import com.facilityflow.dto.response.FloorResponse;
import com.facilityflow.entity.Floor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FloorMapper {

    @Mapping(target = "buildingId", source = "building.id")
    @Mapping(target = "buildingName", source = "building.name")
    @Mapping(target = "roomCount", expression = "java(floor.getRooms() == null ? 0 : floor.getRooms().size())")
    FloorResponse toResponse(Floor floor);
}
