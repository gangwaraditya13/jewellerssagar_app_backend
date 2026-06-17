package com.sagar.jewellery.service.impl;

import com.sagar.jewellery.model.Product;
import com.sagar.jewellery.model.enums.MetalType;
import com.sagar.jewellery.service.PriceCalculationService;
import org.springframework.stereotype.Service;

@Service
public class PriceCalculationServiceImpl implements PriceCalculationService {

    @Override
    public double calculateLivePrice(Product product) {
        if (product.getMetalType() == null) {
            return 0.0;
        }
        return switch (product.getMetalType()) {
            case GOLD -> 75.0;
            case SILVER -> 1.0;
            case PLATINUM -> 35.0;
        };
    }

    @Override
    public double calculateFinalPrice(Product product) {
        double livePricePerGram = calculateLivePrice(product);
        double fineness = (product.getMetalType() == MetalType.GOLD && product.getPurity() != null)
                ? product.getPurity().getFineness()
                : 1.0;
        double materialCost = product.getGrossWeight() * livePricePerGram * fineness;
        double makingCost = product.getGrossWeight() * product.getMakingChargesPerGram();
        return Math.round((materialCost + makingCost) * 100.0) / 100.0;
    }
}
