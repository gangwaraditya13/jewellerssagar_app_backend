package com.sagar.jewellery.model;

import com.sagar.jewellery.model.embedded.Address;
import com.sagar.jewellery.model.embedded.OrderItem;
import com.sagar.jewellery.model.embedded.StatusHistory;
import com.sagar.jewellery.model.enums.OrderStatus;
import com.sagar.jewellery.model.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "orders")
public class Order {
    @Id
    private String id;
    private String userId;
    private String makerId;
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();
    private double totalPrice;
    private OrderStatus status;
    @Builder.Default
    private List<StatusHistory> statusHistory = new ArrayList<>();
    private Address shippingAddress;
    private PaymentMethod paymentMethod;
    private String paymentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
