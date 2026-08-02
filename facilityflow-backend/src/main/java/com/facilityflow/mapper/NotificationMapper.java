package com.facilityflow.mapper;

import com.facilityflow.dto.response.NotificationResponse;
import com.facilityflow.entity.Notification;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    NotificationResponse toResponse(Notification notification);
}
