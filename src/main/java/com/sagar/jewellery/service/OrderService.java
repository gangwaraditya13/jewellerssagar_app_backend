package com.sagar.jewellery.service;

import com.sagar.jewellery.dto.OrderPlaceRequest;
import com.sagar.jewellery.dto.OrderResponse;
import com.sagar.jewellery.model.enums.OrderStatus;

import java.util.List;

public interface OrderService {
    List<OrderResponse> placeOrder(String userId, OrderPlaceRequest request);
    List<OrderResponse> getUserOrders(String userId);
    OrderResponse getOrderById(String orderId, String userId, String role);
    List<OrderResponse> getMakerOrders(String makerId);
    OrderResponse updateOrderStatus(String orderId, OrderStatus status, String updatedBy, String comment);
    OrderResponse cancelOrder(String orderId, String userId);
    List<OrderResponse> getAllOrdersForAdmin();
}
