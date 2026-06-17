package com.sagar.jewellery.service;

import com.sagar.jewellery.dto.Model3DResponse;
import org.springframework.web.multipart.MultipartFile;

public interface Model3DService {
    Model3DResponse uploadModel(String productId, MultipartFile file, String uploadedBy);
    Model3DResponse getModelByProductId(String productId);
    void deleteModel(String id, String deletedBy, String role);
}
