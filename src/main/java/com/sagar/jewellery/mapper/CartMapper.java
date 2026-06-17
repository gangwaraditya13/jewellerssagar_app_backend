package com.sagar.jewellery.mapper;

import com.sagar.jewellery.dto.CartItemResponse;
import com.sagar.jewellery.dto.CartResponse;
import com.sagar.jewellery.model.Cart;
import com.sagar.jewellery.model.embedded.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {
    @Mapping(target = "totalCartPrice", ignore = true)
    CartResponse toResponse(Cart cart);

    @Mapping(target = "productName", ignore = true)
    @Mapping(target = "grossWeight", ignore = true)
    @Mapping(target = "purity", ignore = true)
    @Mapping(target = "metalType", ignore = true)
    @Mapping(target = "makingChargesPerGram", ignore = true)
    @Mapping(target = "livePrice", ignore = true)
    @Mapping(target = "finalPrice", ignore = true)
    @Mapping(target = "subTotal", ignore = true)
    CartItemResponse toItemResponse(CartItem item);
}
