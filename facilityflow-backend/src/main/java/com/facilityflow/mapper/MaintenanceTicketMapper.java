package com.facilityflow.mapper;

import com.facilityflow.dto.response.MaintenanceTicketResponse;
import com.facilityflow.entity.MaintenanceTicket;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = TicketCommentMapper.class)
public interface MaintenanceTicketMapper {

    @Mapping(target = "assetId", source = "asset.id")
    @Mapping(target = "assetName", source = "asset.name")
    @Mapping(target = "roomId", source = "room.id")
    @Mapping(target = "roomName", source = "room.name")
    @Mapping(target = "reportedById", source = "reportedBy.id")
    @Mapping(target = "reportedByName", source = "reportedBy.fullName")
    @Mapping(target = "assignedToId", source = "assignedTo.id")
    @Mapping(target = "assignedToName", source = "assignedTo.fullName")
    MaintenanceTicketResponse toResponse(MaintenanceTicket ticket);
}
