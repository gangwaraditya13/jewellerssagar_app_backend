package com.sagar.jewellery.service;

import com.sagar.jewellery.dto.ReviewRequest;
import com.sagar.jewellery.dto.ReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {
    ReviewResponse createReview(String userId, ReviewRequest request);
    Page<ReviewResponse> getProductReviews(String productId, Pageable pageable);
    void deleteReview(String id);
}
