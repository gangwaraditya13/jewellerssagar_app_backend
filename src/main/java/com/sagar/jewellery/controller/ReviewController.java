package com.sagar.jewellery.controller;

import com.sagar.jewellery.dto.ReviewRequest;
import com.sagar.jewellery.dto.ReviewResponse;
import com.sagar.jewellery.exception.ForbiddenException;
import com.sagar.jewellery.exception.ResourceNotFoundException;
import com.sagar.jewellery.model.Review;
import com.sagar.jewellery.repository.ReviewRepository;
import com.sagar.jewellery.security.CustomUserDetails;
import com.sagar.jewellery.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewRepository reviewRepository;

    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ReviewRequest request) {
        if (!"USER".equals(userDetails.getRole())) {
            throw new ForbiddenException("Only customers can submit reviews");
        }
        ReviewResponse response = reviewService.createReview(userDetails.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<Page<ReviewResponse>> getProductReviews(
            @PathVariable String productId,
            Pageable pageable) {
        return ResponseEntity.ok(reviewService.getProductReviews(productId, pageable));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable String id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + id));

        if (!"ADMIN".equals(userDetails.getRole()) && !review.getUserId().equals(userDetails.getId())) {
            throw new ForbiddenException("You do not have permission to delete this review");
        }

        reviewService.deleteReview(id);
        return ResponseEntity.ok().build();
    }
}
