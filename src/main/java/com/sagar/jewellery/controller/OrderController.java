package com.sagar.jewellery.controller;

import com.sagar.jewellery.dto.OrderPlaceRequest;
import com.sagar.jewellery.dto.OrderResponse;
import com.sagar.jewellery.exception.ForbiddenException;
import com.sagar.jewellery.model.enums.OrderStatus;
import com.sagar.jewellery.security.CustomUserDetails;
import com.sagar.jewellery.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<List<OrderResponse>> placeOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody OrderPlaceRequest request) {
        List<OrderResponse> orders = orderService.placeOrder(userDetails.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(orders);
    }

    @GetMapping("/user")
    public ResponseEntity<List<OrderResponse>> getUserOrders(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (!"USER".equals(userDetails.getRole())) {
            throw new ForbiddenException("Only customers can retrieve their user orders");
        }
        return ResponseEntity.ok(orderService.getUserOrders(userDetails.getId()));
    }

    @GetMapping("/maker")
    public ResponseEntity<List<OrderResponse>> getMakerOrders(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (!"MAKER".equals(userDetails.getRole())) {
            throw new ForbiddenException("Only makers can retrieve their incoming orders");
        }
        return ResponseEntity.ok(orderService.getMakerOrders(userDetails.getId()));
    }

    @GetMapping("/admin")
    public ResponseEntity<List<OrderResponse>> getAllOrdersForAdmin(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (!"ADMIN".equals(userDetails.getRole())) {
            throw new ForbiddenException("Only administrators can retrieve all orders");
        }
        return ResponseEntity.ok(orderService.getAllOrdersForAdmin());
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(
            @PathVariable String orderId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        OrderResponse order = orderService.getOrderById(orderId, userDetails.getId(), userDetails.getRole());
        return ResponseEntity.ok(order);
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable String orderId,
            @RequestParam OrderStatus status,
            @RequestParam(required = false) String comment,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        OrderResponse updatedOrder = orderService.updateOrderStatus(orderId, status, userDetails.getId(), comment);
        return ResponseEntity.ok(updatedOrder);
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(
            @PathVariable String orderId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        OrderResponse cancelledOrder = orderService.cancelOrder(orderId, userDetails.getId());
        return ResponseEntity.ok(cancelledOrder);
    }
}
