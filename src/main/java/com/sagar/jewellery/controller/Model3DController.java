package com.sagar.jewellery.controller;

import com.sagar.jewellery.dto.Model3DResponse;
import com.sagar.jewellery.security.CustomUserDetails;
import com.sagar.jewellery.service.Model3DService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/models")
public class Model3DController {

    @Autowired
    private Model3DService model3DService;

    @PostMapping("/upload")
    public ResponseEntity<Model3DResponse> uploadModel(
            @RequestParam("productId") String productId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Model3DResponse response = model3DService.uploadModel(productId, file, userDetails.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<Model3DResponse> getModelByProductId(@PathVariable String productId) {
        return ResponseEntity.ok(model3DService.getModelByProductId(productId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteModel(
            @PathVariable String id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        model3DService.deleteModel(id, userDetails.getId(), userDetails.getRole());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/file/{filename:.+}")
    public ResponseEntity<Resource> getModelFile(@PathVariable String filename) {
        try {
            Path filePath = Paths.get("uploads/models3d").resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
