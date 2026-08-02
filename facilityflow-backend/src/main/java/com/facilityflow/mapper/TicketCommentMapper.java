package com.facilityflow.mapper;

import com.facilityflow.dto.response.TicketCommentResponse;
import com.facilityflow.entity.TicketComment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TicketCommentMapper {

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "authorName", source = "author.fullName")
    TicketCommentResponse toResponse(TicketComment comment);
}
