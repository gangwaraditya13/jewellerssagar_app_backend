package com.sagar.jewellery.service.impl;

import com.sagar.jewellery.dto.CartItemRequest;
import com.sagar.jewellery.dto.CartItemResponse;
import com.sagar.jewellery.dto.CartResponse;
import com.sagar.jewellery.exception.ResourceNotFoundException;
import com.sagar.jewellery.mapper.CartMapper;
import com.sagar.jewellery.model.Cart;
import com.sagar.jewellery.model.Product;
import com.sagar.jewellery.model.embedded.CartItem;
import com.sagar.jewellery.repository.CartRepository;
import com.sagar.jewellery.repository.ProductRepository;
import com.sagar.jewellery.service.CartService;
import com.sagar.jewellery.service.PriceCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PriceCalculationService priceCalculationService;

    @Autowired
    private CartMapper cartMapper;

    @Override
    public CartResponse getCart(String userId) {
        Cart cart = getOrCreateCart(userId);
        return enrichCartResponse(cart);
    }

    private Cart getOrCreateCart(String userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(
                        Cart.builder()
                                .userId(userId)
                                .items(new ArrayList<>())
                                .updatedAt(LocalDateTime.now())
                                .build()
                ));
    }

    private CartResponse enrichCartResponse(Cart cart) {
        List<CartItemResponse> itemResponses = cart.getItems().stream()
                .map(item -> {
                    Product product = productRepository.findById(item.getProductId()).orElse(null);
                    CartItemResponse response = cartMapper.toItemResponse(item);
                    
                    if (product != null) {
                        response.setProductName(product.getName());
                        response.setGrossWeight(product.getGrossWeight());
                        response.setPurity(product.getPurity());
                        response.setMetalType(product.getMetalType());
                        response.setMakingChargesPerGram(product.getMakingChargesPerGram());
                        
                        double currentLivePrice = priceCalculationService.calculateLivePrice(product);
                        double currentFinalPrice = priceCalculationService.calculateFinalPrice(product);
                        response.setLivePrice(currentLivePrice);
                        response.setFinalPrice(currentFinalPrice);
                        response.setSubTotal(Math.round(currentFinalPrice * item.getQuantity() * 100.0) / 100.0);
                    } else {
                        response.setProductName("Deleted Product");
                        response.setLivePrice(0.0);
                        response.setFinalPrice(item.getPriceSnapshot());
                        response.setSubTotal(Math.round(item.getPriceSnapshot() * item.getQuantity() * 100.0) / 100.0);
                    }
                    return response;
                })
                .collect(Collectors.toList());

        double totalCartPrice = itemResponses.stream()
                .mapToDouble(CartItemResponse::getSubTotal)
                .sum();

        CartResponse response = cartMapper.toResponse(cart);
        response.setItems(itemResponses);
        response.setTotalCartPrice(Math.round(totalCartPrice * 100.0) / 100.0);
        return response;
    }

    @Override
    public CartResponse addItemToCart(String userId, CartItemRequest request) {
        Cart cart = getOrCreateCart(userId);
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + request.getProductId()));

        double finalPrice = priceCalculationService.calculateFinalPrice(product);

        Optional<CartItem> existingItemOpt = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(request.getProductId()))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            CartItem item = existingItemOpt.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
            item.setPriceSnapshot(finalPrice);
            item.setAddedAt(LocalDateTime.now());
        } else {
            cart.getItems().add(CartItem.builder()
                    .productId(request.getProductId())
                    .quantity(request.getQuantity())
                    .priceSnapshot(finalPrice)
                    .addedAt(LocalDateTime.now())
                    .build());
        }

        cart.setUpdatedAt(LocalDateTime.now());
        Cart saved = cartRepository.save(cart);
        return enrichCartResponse(saved);
    }

    @Override
    public CartResponse updateItemQuantity(String userId, String productId, int quantity) {
        Cart cart = getOrCreateCart(userId);
        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Product not found in cart: " + productId));

        Product product = productRepository.findById(productId).orElse(null);
        if (product != null) {
            double finalPrice = priceCalculationService.calculateFinalPrice(product);
            cartItem.setPriceSnapshot(finalPrice);
        }

        cartItem.setQuantity(quantity);
        cartItem.setAddedAt(LocalDateTime.now());
        cart.setUpdatedAt(LocalDateTime.now());
        
        Cart saved = cartRepository.save(cart);
        return enrichCartResponse(saved);
    }

    @Override
    public CartResponse removeItemFromCart(String userId, String productId) {
        Cart cart = getOrCreateCart(userId);
        boolean removed = cart.getItems().removeIf(item -> item.getProductId().equals(productId));
        if (!removed) {
            throw new ResourceNotFoundException("Product not found in cart: " + productId);
        }
        cart.setUpdatedAt(LocalDateTime.now());
        Cart saved = cartRepository.save(cart);
        return enrichCartResponse(saved);
    }

    @Override
    public void clearCart(String userId) {
        Cart cart = getOrCreateCart(userId);
        cart.getItems().clear();
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
    }
}
