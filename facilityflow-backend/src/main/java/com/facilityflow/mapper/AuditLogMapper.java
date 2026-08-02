package com.facilityflow.mapper;

import com.facilityflow.dto.response.AuditLogResponse;
import com.facilityflow.entity.AuditLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuditLogMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userName", source = "user.fullName")
    AuditLogResponse toResponse(AuditLog auditLog);
}
