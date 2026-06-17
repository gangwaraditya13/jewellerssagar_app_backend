package com.sagar.jewellery.dto;

import com.sagar.jewellery.model.enums.GoldPurity;
import com.sagar.jewellery.model.enums.JewelleryCategory;
import com.sagar.jewellery.model.enums.MetalType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductCreateRequest {
    @NotBlank(message = "Product name is required")
    private String name;

    private String description;

    @NotNull(message = "Category is required")
    private JewelleryCategory category;

    @NotNull(message = "Metal type is required")
    private MetalType metalType;

    @NotNull(message = "Purity is required")
    private GoldPurity purity;

    @Min(value = 0, message = "Gross weight must be greater than or equal to 0")
    private double grossWeight;

    @Min(value = 0, message = "Making charges must be greater than or equal to 0")
    private double makingChargesPerGram;

    @Min(value = 0, message = "Stock quantity cannot be negative")
    private int stockQuantity;

    private String imageUrl;
}
