package com.sagar.jewellery.dto;

import com.sagar.jewellery.model.embedded.Address;
import com.sagar.jewellery.model.embedded.OrderItem;
import com.sagar.jewellery.model.embedded.StatusHistory;
import com.sagar.jewellery.model.enums.OrderStatus;
import com.sagar.jewellery.model.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private String id;
    private String userId;
    private String makerId;
    private List<OrderItem> items;
    private double totalPrice;
    private OrderStatus status;
    private List<StatusHistory> statusHistory;
    private Address shippingAddress;
    private PaymentMethod paymentMethod;
    private String paymentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
