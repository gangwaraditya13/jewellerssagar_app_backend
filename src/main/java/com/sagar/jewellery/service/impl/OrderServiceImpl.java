package com.sagar.jewellery.service.impl;

import com.sagar.jewellery.dto.OrderPlaceRequest;
import com.sagar.jewellery.dto.OrderResponse;
import com.sagar.jewellery.exception.ForbiddenException;
import com.sagar.jewellery.exception.InsufficientStockException;
import com.sagar.jewellery.exception.ResourceNotFoundException;
import com.sagar.jewellery.mapper.OrderMapper;
import com.sagar.jewellery.model.Cart;
import com.sagar.jewellery.model.Order;
import com.sagar.jewellery.model.Payment;
import com.sagar.jewellery.model.Product;
import com.sagar.jewellery.model.embedded.OrderItem;
import com.sagar.jewellery.model.embedded.StatusHistory;
import com.sagar.jewellery.model.enums.OrderStatus;
import com.sagar.jewellery.model.enums.PaymentStatus;
import com.sagar.jewellery.repository.CartRepository;
import com.sagar.jewellery.repository.OrderRepository;
import com.sagar.jewellery.repository.PaymentRepository;
import com.sagar.jewellery.repository.ProductRepository;
import com.sagar.jewellery.service.OrderService;
import com.sagar.jewellery.service.PriceCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PriceCalculationService priceCalculationService;

    @Autowired
    private OrderMapper orderMapper;

    @Override
    @Transactional
    public List<OrderResponse> placeOrder(String userId, OrderPlaceRequest request) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user: " + userId));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cannot place order with an empty cart");
        }

        Map<String, List<OrderItem>> makerItemsMap = new HashMap<>();

        for (var item : cart.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + item.getProductId()));

            if (product.getStockQuantity() < item.getQuantity()) {
                throw new InsufficientStockException("Insufficient stock for product: " + product.getName() 
                        + ". Available: " + product.getStockQuantity() + ", Requested: " + item.getQuantity());
            }

            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
            productRepository.save(product);

            double finalPrice = priceCalculationService.calculateFinalPrice(product);

            OrderItem orderItem = OrderItem.builder()
                    .productId(product.getId())
                    .productName(product.getName())
                    .quantity(item.getQuantity())
                    .grossWeight(product.getGrossWeight())
                    .purity(product.getPurity())
                    .metalType(product.getMetalType())
                    .makingChargesPerGram(product.getMakingChargesPerGram())
                    .priceSnapshot(finalPrice)
                    .build();

            makerItemsMap.computeIfAbsent(product.getMakerId(), k -> new ArrayList<>()).add(orderItem);
        }

        List<Order> createdOrders = new ArrayList<>();

        for (var entry : makerItemsMap.entrySet()) {
            String makerId = entry.getKey();
            List<OrderItem> items = entry.getValue();

            double totalPrice = items.stream()
                    .mapToDouble(i -> i.getPriceSnapshot() * i.getQuantity())
                    .sum();
            totalPrice = Math.round(totalPrice * 100.0) / 100.0;

            StatusHistory initialHistory = StatusHistory.builder()
                    .status(OrderStatus.PENDING)
                    .updatedBy(userId)
                    .updatedAt(LocalDateTime.now())
                    .comment("Order placed successfully")
                    .build();

            Order order = Order.builder()
                    .userId(userId)
                    .makerId(makerId)
                    .items(items)
                    .totalPrice(totalPrice)
                    .status(OrderStatus.PENDING)
                    .statusHistory(new ArrayList<>(List.of(initialHistory)))
                    .shippingAddress(request.getShippingAddress())
                    .paymentMethod(request.getPaymentMethod())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            Order savedOrder = orderRepository.save(order);

            Payment payment = Payment.builder()
                    .orderId(savedOrder.getId())
                    .userId(userId)
                    .amount(totalPrice)
                    .method(request.getPaymentMethod())
                    .status(PaymentStatus.PENDING)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            Payment savedPayment = paymentRepository.save(payment);
            savedOrder.setPaymentId(savedPayment.getId());
            orderRepository.save(savedOrder);

            createdOrders.add(savedOrder);
        }

        cart.getItems().clear();
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);

        return createdOrders.stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponse> getUserOrders(String userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponse getOrderById(String orderId, String userId, String role) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if ("USER".equals(role) && !order.getUserId().equals(userId)) {
            throw new ForbiddenException("You are not authorized to view this order");
        }
        if ("MAKER".equals(role) && !order.getMakerId().equals(userId)) {
            throw new ForbiddenException("You are not authorized to view this order");
        }

        return orderMapper.toResponse(order);
    }

    @Override
    public List<OrderResponse> getMakerOrders(String makerId) {
        return orderRepository.findByMakerId(makerId).stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(String orderId, OrderStatus status, String updatedBy, String comment) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (!order.getMakerId().equals(updatedBy)) {
            throw new ForbiddenException("You do not have permission to update the status of this order");
        }

        List<OrderStatus> allowedStates = List.of(OrderStatus.ACCEPTED, OrderStatus.IN_PROCESS, OrderStatus.COMPLETED, OrderStatus.DECLINED);
        if (!allowedStates.contains(status)) {
            throw new IllegalArgumentException("Makers can only update order status to ACCEPTED, IN_PROCESS, COMPLETED, or DECLINED");
        }

        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        
        StatusHistory history = StatusHistory.builder()
                .status(status)
                .updatedBy(updatedBy)
                .updatedAt(LocalDateTime.now())
                .comment(comment != null ? comment : "Status updated by maker")
                .build();
        order.getStatusHistory().add(history);

        Order saved = orderRepository.save(order);
        return orderMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(String orderId, String userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (!order.getUserId().equals(userId)) {
            throw new ForbiddenException("You do not have permission to cancel this order");
        }

        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.ACCEPTED) {
            throw new IllegalArgumentException("Orders can only be cancelled while in PENDING or ACCEPTED status");
        }

        for (var item : order.getItems()) {
            productRepository.findById(item.getProductId()).ifPresent(product -> {
                product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
                productRepository.save(product);
            });
        }

        order.setStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());

        StatusHistory history = StatusHistory.builder()
                .status(OrderStatus.CANCELLED)
                .updatedBy(userId)
                .updatedAt(LocalDateTime.now())
                .comment("Cancelled by user")
                .build();
        order.getStatusHistory().add(history);

        paymentRepository.findByOrderId(orderId).ifPresent(payment -> {
            if (payment.getStatus() == PaymentStatus.COMPLETED) {
                payment.setStatus(PaymentStatus.REFUNDED);
            } else {
                payment.setStatus(PaymentStatus.FAILED);
            }
            payment.setUpdatedAt(LocalDateTime.now());
            paymentRepository.save(payment);
        });

        Order saved = orderRepository.save(order);
        return orderMapper.toResponse(saved);
    }

    @Override
    public List<OrderResponse> getAllOrdersForAdmin() {
        return orderRepository.findAll().stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }
}
