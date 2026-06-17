package com.sagar.jewellery.mapper;

import com.sagar.jewellery.dto.UserProfileResponse;
import com.sagar.jewellery.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserProfileResponse toProfileResponse(User user);
}
