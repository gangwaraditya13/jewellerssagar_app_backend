package com.sagar.jewellery.service.impl;

import com.sagar.jewellery.dto.ReviewRequest;
import com.sagar.jewellery.dto.ReviewResponse;
import com.sagar.jewellery.exception.ResourceNotFoundException;
import com.sagar.jewellery.mapper.ReviewMapper;
import com.sagar.jewellery.model.Order;
import com.sagar.jewellery.model.Review;
import com.sagar.jewellery.model.User;
import com.sagar.jewellery.model.enums.OrderStatus;
import com.sagar.jewellery.repository.OrderRepository;
import com.sagar.jewellery.repository.ReviewRepository;
import com.sagar.jewellery.repository.UserRepository;
import com.sagar.jewellery.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ReviewMapper reviewMapper;

    @Override
    public ReviewResponse createReview(String userId, ReviewRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        List<Order> userOrders = orderRepository.findByUserId(userId);
        boolean hasPurchased = userOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.COMPLETED)
                .flatMap(o -> o.getItems().stream())
                .anyMatch(item -> item.getProductId().equals(request.getProductId()));

        if (!hasPurchased) {
            throw new IllegalArgumentException("You can only review products that you have successfully purchased.");
        }

        Review review = reviewMapper.toEntity(request);
        review.setUserId(userId);
        review.setUserName(user.getFullName());
        review.setCreatedAt(LocalDateTime.now());

        Review saved = reviewRepository.save(review);
        return reviewMapper.toResponse(saved);
    }

    @Override
    public Page<ReviewResponse> getProductReviews(String productId, Pageable pageable) {
        return reviewRepository.findByProductId(productId, pageable)
                .map(reviewMapper::toResponse);
    }

    @Override
    public void deleteReview(String id) {
        if (!reviewRepository.existsById(id)) {
            throw new ResourceNotFoundException("Review not found with id: " + id);
        }
        reviewRepository.deleteById(id);
    }
}
