package com.sagar.jewellery.mapper;

import com.sagar.jewellery.dto.ReviewRequest;
import com.sagar.jewellery.dto.ReviewResponse;
import com.sagar.jewellery.model.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "userName", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Review toEntity(ReviewRequest request);

    ReviewResponse toResponse(Review review);
}
