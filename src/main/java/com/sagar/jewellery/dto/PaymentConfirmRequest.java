package com.sagar.jewellery.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PaymentConfirmRequest {
    @NotBlank(message = "Order ID is required")
    private String orderId;

    @NotBlank(message = "Transaction reference is required")
    private String transactionReference;

    @NotBlank(message = "Status is required (COMPLETED / FAILED)")
    private String status;
}
