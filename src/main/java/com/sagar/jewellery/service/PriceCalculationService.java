package com.sagar.jewellery.service;

import com.sagar.jewellery.model.Product;

public interface PriceCalculationService {
    double calculateLivePrice(Product product);
    double calculateFinalPrice(Product product);
}
