package com.thaisb.webfluxcourse.mapper;

import com.thaisb.webfluxcourse.entity.User;
import com.thaisb.webfluxcourse.model.request.UserRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring"
)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    User toEntity(final UserRequest request);

}
