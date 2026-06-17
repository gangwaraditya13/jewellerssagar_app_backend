package com.sagar.jewellery.service.impl;

import com.sagar.jewellery.dto.Model3DResponse;
import com.sagar.jewellery.exception.ForbiddenException;
import com.sagar.jewellery.exception.ResourceNotFoundException;
import com.sagar.jewellery.model.Model3D;
import com.sagar.jewellery.model.Product;
import com.sagar.jewellery.repository.Model3DRepository;
import com.sagar.jewellery.repository.ProductRepository;
import com.sagar.jewellery.service.Model3DService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class Model3DServiceImpl implements Model3DService {

    @Autowired
    private Model3DRepository model3DRepository;

    @Autowired
    private ProductRepository productRepository;

    private final Path rootPath = Paths.get("uploads/models3d");

    public Model3DServiceImpl() {
        try {
            Files.createDirectories(rootPath);
        } catch (IOException e) {
            // Root path creation error logged
        }
    }

    @Override
    public Model3DResponse uploadModel(String productId, MultipartFile file, String uploadedBy) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        if (!product.getMakerId().equals(uploadedBy)) {
            throw new ForbiddenException("You are not authorized to upload a 3D model for this product");
        }

        try {
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path targetLocation = rootPath.resolve(filename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            String fileUrl = "/api/models/file/" + filename;

            model3DRepository.findByProductId(productId).ifPresent(oldModel -> {
                try {
                    String oldFilename = oldModel.getFileUrl().substring(oldModel.getFileUrl().lastIndexOf("/") + 1);
                    Files.deleteIfExists(rootPath.resolve(oldFilename));
                } catch (IOException e) {
                    // Log old file deletion error
                }
                model3DRepository.delete(oldModel);
            });

            Model3D model3D = Model3D.builder()
                    .productId(productId)
                    .fileUrl(fileUrl)
                    .fileSize(file.getSize())
                    .uploadedBy(uploadedBy)
                    .uploadedAt(LocalDateTime.now())
                    .build();

            Model3D saved = model3DRepository.save(model3D);

            product.setModel3DId(saved.getId());
            productRepository.save(product);

            return toResponse(saved);
        } catch (IOException ex) {
            throw new RuntimeException("Could not store 3D model. Please try again!", ex);
        }
    }

    @Override
    public Model3DResponse getModelByProductId(String productId) {
        Model3D model3D = model3DRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("3D Model not found for product id: " + productId));
        return toResponse(model3D);
    }

    @Override
    public void deleteModel(String id, String deletedBy, String role) {
        Model3D model = model3DRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("3D Model not found with id: " + id));

        Product product = productRepository.findById(model.getProductId()).orElse(null);

        if ("MAKER".equals(role) && !model.getUploadedBy().equals(deletedBy)) {
            throw new ForbiddenException("You are not authorized to delete this 3D model");
        }

        try {
            String filename = model.getFileUrl().substring(model.getFileUrl().lastIndexOf("/") + 1);
            Files.deleteIfExists(rootPath.resolve(filename));
        } catch (IOException e) {
            // Log file deletion error
        }

        if (product != null) {
            product.setModel3DId(null);
            productRepository.save(product);
        }

        model3DRepository.deleteById(id);
    }

    private Model3DResponse toResponse(Model3D model) {
        return Model3DResponse.builder()
                .id(model.getId())
                .productId(model.getProductId())
                .fileUrl(model.getFileUrl())
                .fileSize(model.getFileSize())
                .uploadedBy(model.getUploadedBy())
                .uploadedAt(model.getUploadedAt())
                .build();
    }
}
