package com.sagar.jewellery.service.impl;

import com.sagar.jewellery.dto.ProductCreateRequest;
import com.sagar.jewellery.dto.ProductResponse;
import com.sagar.jewellery.exception.ForbiddenException;
import com.sagar.jewellery.exception.ResourceNotFoundException;
import com.sagar.jewellery.mapper.ProductMapper;
import com.sagar.jewellery.model.Product;
import com.sagar.jewellery.model.enums.GoldPurity;
import com.sagar.jewellery.model.enums.JewelleryCategory;
import com.sagar.jewellery.model.enums.MetalType;
import com.sagar.jewellery.repository.ProductRepository;
import com.sagar.jewellery.service.PriceCalculationService;
import com.sagar.jewellery.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PriceCalculationService priceCalculationService;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    @Cacheable(value = "products", key = "{#category, #metalType, #purity, #pageable.pageNumber, #pageable.pageSize, #pageable.sort}")
    public Page<ProductResponse> getAllProducts(JewelleryCategory category, MetalType metalType, GoldPurity purity, Pageable pageable) {
        Query query = new Query();
        if (category != null) {
            query.addCriteria(Criteria.where("category").is(category));
        }
        if (metalType != null) {
            query.addCriteria(Criteria.where("metalType").is(metalType));
        }
        if (purity != null) {
            query.addCriteria(Criteria.where("purity").is(purity));
        }

        long total = mongoTemplate.count(query, Product.class);
        query.with(pageable);
        List<Product> products = mongoTemplate.find(query, Product.class);

        List<ProductResponse> responses = products.stream()
                .map(this::enrichProductResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, total);
    }

    private ProductResponse enrichProductResponse(Product product) {
        ProductResponse response = productMapper.toResponse(product);
        response.setLivePrice(priceCalculationService.calculateLivePrice(product));
        response.setFinalPrice(priceCalculationService.calculateFinalPrice(product));
        return response;
    }

    @Override
    @Cacheable(value = "productDetails", key = "#id")
    public ProductResponse getProduct(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return enrichProductResponse(product);
    }

    @Override
    @CacheEvict(value = "products", allEntries = true)
    public ProductResponse createProduct(ProductCreateRequest request, String makerId) {
        Product product = productMapper.toEntity(request);
        product.setMakerId(makerId);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        Product saved = productRepository.save(product);
        return enrichProductResponse(saved);
    }

    @Override
    @Caching(evict = {
        @CacheEvict(value = "products", allEntries = true),
        @CacheEvict(value = "productDetails", key = "#id")
    })
    public ProductResponse updateProduct(String id, ProductCreateRequest request, String updaterId, String updaterRole) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        if ("MAKER".equals(updaterRole) && !product.getMakerId().equals(updaterId)) {
            throw new ForbiddenException("You do not have permission to update this product");
        }

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setCategory(request.getCategory());
        product.setMetalType(request.getMetalType());
        product.setPurity(request.getPurity());
        product.setGrossWeight(request.getGrossWeight());
        product.setMakingChargesPerGram(request.getMakingChargesPerGram());
        product.setStockQuantity(request.getStockQuantity());
        if (request.getImageUrl() != null) {
            product.setImageUrl(request.getImageUrl());
        }
        product.setUpdatedAt(LocalDateTime.now());

        Product saved = productRepository.save(product);
        return enrichProductResponse(saved);
    }

    @Override
    @Caching(evict = {
        @CacheEvict(value = "products", allEntries = true),
        @CacheEvict(value = "productDetails", key = "#id")
    })
    public void deleteProduct(String id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    @Override
    public List<ProductResponse> getMakerProducts(String makerId) {
        return productRepository.findByMakerId(makerId).stream()
                .map(this::enrichProductResponse)
                .collect(Collectors.toList());
    }
}
