package com.sagar.jewellery.mapper;

import com.sagar.jewellery.dto.MakerProfileResponse;
import com.sagar.jewellery.model.JewelleryMaker;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MakerMapper {
    @Mapping(source = "approved", target = "isApproved")
    MakerProfileResponse toProfileResponse(JewelleryMaker maker);
}
