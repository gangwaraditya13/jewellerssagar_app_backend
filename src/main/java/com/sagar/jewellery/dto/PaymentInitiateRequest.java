package com.sagar.jewellery.dto;

import com.sagar.jewellery.model.enums.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentInitiateRequest {
    @NotBlank(message = "Order ID is required")
    private String orderId;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;
}
