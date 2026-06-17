package com.sagar.jewellery.service;

import com.sagar.jewellery.dto.ProductCreateRequest;
import com.sagar.jewellery.dto.ProductResponse;
import com.sagar.jewellery.model.enums.GoldPurity;
import com.sagar.jewellery.model.enums.JewelleryCategory;
import com.sagar.jewellery.model.enums.MetalType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    Page<ProductResponse> getAllProducts(JewelleryCategory category, MetalType metalType, GoldPurity purity, Pageable pageable);
    ProductResponse getProduct(String id);
    ProductResponse createProduct(ProductCreateRequest request, String makerId);
    ProductResponse updateProduct(String id, ProductCreateRequest request, String updaterId, String updaterRole);
    void deleteProduct(String id);
    List<ProductResponse> getMakerProducts(String makerId);
}
