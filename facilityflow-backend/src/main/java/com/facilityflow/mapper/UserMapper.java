package com.facilityflow.mapper;

import com.facilityflow.dto.response.UserResponse;
import com.facilityflow.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toResponse(User user);
}
