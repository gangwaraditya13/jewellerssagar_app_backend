package com.sagar.jewellery.controller;

import com.sagar.jewellery.dto.PaymentConfirmRequest;
import com.sagar.jewellery.dto.PaymentInitiateRequest;
import com.sagar.jewellery.dto.PaymentResponse;
import com.sagar.jewellery.security.CustomUserDetails;
import com.sagar.jewellery.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/initiate")
    public ResponseEntity<PaymentResponse> initiatePayment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PaymentInitiateRequest request) {
        PaymentResponse response = paymentService.initiatePayment(userDetails.getId(), request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/confirm")
    public ResponseEntity<PaymentResponse> confirmPayment(
            @Valid @RequestBody PaymentConfirmRequest request) {
        PaymentResponse response = paymentService.confirmPayment(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponse> getPaymentByOrderId(
            @PathVariable String orderId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        PaymentResponse response = paymentService.getPaymentByOrderId(orderId, userDetails.getId(), userDetails.getRole());
        return ResponseEntity.ok(response);
    }
}
