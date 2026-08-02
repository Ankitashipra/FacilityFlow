package com.facilityflow.mapper;

import com.facilityflow.dto.response.AssetResponse;
import com.facilityflow.entity.Asset;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AssetMapper {

    @Mapping(target = "roomId", source = "room.id")
    @Mapping(target = "roomName", source = "room.name")
    AssetResponse toResponse(Asset asset);
}
