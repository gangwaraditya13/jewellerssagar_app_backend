package com.sagar.jewellery.model.embedded;

import com.sagar.jewellery.model.enums.GoldPurity;
import com.sagar.jewellery.model.enums.MetalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    private String productId;
    private String productName;
    private int quantity;
    private double grossWeight;
    private GoldPurity purity;
    private MetalType metalType;
    private double makingChargesPerGram;
    private double priceSnapshot;
}
