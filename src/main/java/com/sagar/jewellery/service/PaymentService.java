package com.sagar.jewellery.service;

import com.sagar.jewellery.dto.PaymentConfirmRequest;
import com.sagar.jewellery.dto.PaymentInitiateRequest;
import com.sagar.jewellery.dto.PaymentResponse;

public interface PaymentService {
    PaymentResponse initiatePayment(String userId, PaymentInitiateRequest request);
    PaymentResponse confirmPayment(PaymentConfirmRequest request);
    PaymentResponse getPaymentByOrderId(String orderId, String userId, String role);
}
