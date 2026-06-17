package com.sagar.jewellery.mapper;

import com.sagar.jewellery.dto.OrderResponse;
import com.sagar.jewellery.model.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderResponse toResponse(Order order);
}
