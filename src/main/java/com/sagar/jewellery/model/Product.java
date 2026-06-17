package com.sagar.jewellery.model;

import com.sagar.jewellery.model.enums.GoldPurity;
import com.sagar.jewellery.model.enums.JewelleryCategory;
import com.sagar.jewellery.model.enums.MetalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "products")
public class Product {
    @Id
    private String id;
    private String name;
    private String description;
    private JewelleryCategory category;
    private MetalType metalType;
    private GoldPurity purity;
    private double grossWeight;
    private double makingChargesPerGram;
    private int stockQuantity;
    private String makerId;
    private String imageUrl;
    private String model3DId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
