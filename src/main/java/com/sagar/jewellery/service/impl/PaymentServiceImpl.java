package com.sagar.jewellery.service.impl;

import com.sagar.jewellery.dto.PaymentConfirmRequest;
import com.sagar.jewellery.dto.PaymentInitiateRequest;
import com.sagar.jewellery.dto.PaymentResponse;
import com.sagar.jewellery.exception.ForbiddenException;
import com.sagar.jewellery.exception.ResourceNotFoundException;
import com.sagar.jewellery.model.Order;
import com.sagar.jewellery.model.Payment;
import com.sagar.jewellery.model.enums.PaymentStatus;
import com.sagar.jewellery.repository.OrderRepository;
import com.sagar.jewellery.repository.PaymentRepository;
import com.sagar.jewellery.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public PaymentResponse initiatePayment(String userId, PaymentInitiateRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + request.getOrderId()));

        if (!order.getUserId().equals(userId)) {
            throw new ForbiddenException("You are not authorized to initiate payment for this order");
        }

        Payment payment = paymentRepository.findByOrderId(request.getOrderId())
                .orElseGet(() -> Payment.builder()
                        .orderId(request.getOrderId())
                        .userId(userId)
                        .amount(order.getTotalPrice())
                        .method(order.getPaymentMethod())
                        .status(PaymentStatus.PENDING)
                        .createdAt(LocalDateTime.now())
                        .build());

        payment.setUpdatedAt(LocalDateTime.now());
        Payment saved = paymentRepository.save(payment);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public PaymentResponse confirmPayment(PaymentConfirmRequest request) {
        Payment payment = paymentRepository.findByOrderId(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for order id: " + request.getOrderId()));

        if ("COMPLETED".equalsIgnoreCase(request.getStatus())) {
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setTransactionReference(request.getTransactionReference() != null 
                    ? request.getTransactionReference() 
                    : UUID.randomUUID().toString());
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            if (request.getTransactionReference() != null) {
                payment.setTransactionReference(request.getTransactionReference());
            }
        }

        payment.setUpdatedAt(LocalDateTime.now());
        Payment saved = paymentRepository.save(payment);
        return toResponse(saved);
    }

    @Override
    public PaymentResponse getPaymentByOrderId(String orderId, String userId, String role) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for order id: " + orderId));

        if ("USER".equals(role) && !payment.getUserId().equals(userId)) {
            throw new ForbiddenException("You are not authorized to view this payment");
        }

        if ("MAKER".equals(role)) {
            Order order = orderRepository.findById(orderId).orElse(null);
            if (order == null || !order.getMakerId().equals(userId)) {
                throw new ForbiddenException("You are not authorized to view this payment");
            }
        }

        return toResponse(payment);
    }

    private PaymentResponse toResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrderId())
                .userId(payment.getUserId())
                .amount(payment.getAmount())
                .method(payment.getMethod())
                .status(payment.getStatus())
                .transactionReference(payment.getTransactionReference())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
}
