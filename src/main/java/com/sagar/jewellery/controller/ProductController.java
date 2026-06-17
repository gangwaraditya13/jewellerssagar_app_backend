package com.sagar.jewellery.controller;

import com.sagar.jewellery.dto.ProductCreateRequest;
import com.sagar.jewellery.dto.ProductResponse;
import com.sagar.jewellery.model.enums.GoldPurity;
import com.sagar.jewellery.model.enums.JewelleryCategory;
import com.sagar.jewellery.model.enums.MetalType;
import com.sagar.jewellery.security.CustomUserDetails;
import com.sagar.jewellery.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getAllProducts(
            @RequestParam(required = false) JewelleryCategory category,
            @RequestParam(required = false) MetalType metalType,
            @RequestParam(required = false) GoldPurity purity,
            Pageable pageable) {
        Page<ProductResponse> products = productService.getAllProducts(category, metalType, purity, pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable String id) {
        return ResponseEntity.ok(productService.getProduct(id));
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ProductCreateRequest request) {
        ProductResponse response = productService.createProduct(request, userDetails.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable String id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ProductCreateRequest request) {
        ProductResponse response = productService.updateProduct(id, request, userDetails.getId(), userDetails.getRole());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/maker/my-products")
    public ResponseEntity<List<ProductResponse>> getMakerProducts(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(productService.getMakerProducts(userDetails.getId()));
    }
}
