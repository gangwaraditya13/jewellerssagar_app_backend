package com.sagar.jewellery.service;

import com.sagar.jewellery.dto.CartItemRequest;
import com.sagar.jewellery.dto.CartResponse;

public interface CartService {
    CartResponse getCart(String userId);
    CartResponse addItemToCart(String userId, CartItemRequest request);
    CartResponse updateItemQuantity(String userId, String productId, int quantity);
    CartResponse removeItemFromCart(String userId, String productId);
    void clearCart(String userId);
}
