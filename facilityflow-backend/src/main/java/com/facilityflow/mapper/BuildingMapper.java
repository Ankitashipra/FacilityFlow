package com.facilityflow.mapper;

import com.facilityflow.dto.response.BuildingResponse;
import com.facilityflow.entity.Building;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BuildingMapper {

    @Mapping(target = "floorCount", expression = "java(building.getFloors() == null ? 0 : building.getFloors().size())")
    BuildingResponse toResponse(Building building);
}
