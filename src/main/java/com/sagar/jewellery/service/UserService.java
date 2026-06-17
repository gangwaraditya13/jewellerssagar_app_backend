package com.sagar.jewellery.service;

import com.sagar.jewellery.dto.UserProfileResponse;
import com.sagar.jewellery.dto.UserUpdateRequest;
import com.sagar.jewellery.model.embedded.Address;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {
    UserProfileResponse getProfile(String userId);
    UserProfileResponse updateProfile(String userId, UserUpdateRequest request);
    UserProfileResponse updatePermanentAddress(String userId, Address address);
    UserProfileResponse updateLiveAddress(String userId, Address address);
    String uploadGalleryImage(String userId, MultipartFile file);
    List<String> getGalleryImages(String userId);
}
