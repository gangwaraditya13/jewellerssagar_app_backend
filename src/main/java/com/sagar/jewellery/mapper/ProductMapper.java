package com.sagar.jewellery.mapper;

import com.sagar.jewellery.dto.ProductCreateRequest;
import com.sagar.jewellery.dto.ProductResponse;
import com.sagar.jewellery.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "makerId", ignore = true)
    @Mapping(target = "model3DId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Product toEntity(ProductCreateRequest request);

    @Mapping(target = "livePrice", ignore = true)
    @Mapping(target = "finalPrice", ignore = true)
    ProductResponse toResponse(Product product);
}
