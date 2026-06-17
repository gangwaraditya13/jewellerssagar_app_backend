package com.sagar.jewellery.dto;

import com.sagar.jewellery.model.embedded.Address;
import com.sagar.jewellery.model.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderPlaceRequest {
    @NotNull(message = "Shipping address is required")
    private Address shippingAddress;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;
}
