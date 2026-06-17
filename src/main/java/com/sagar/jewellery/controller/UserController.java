package com.sagar.jewellery.controller;

import com.sagar.jewellery.dto.UserProfileResponse;
import com.sagar.jewellery.dto.UserUpdateRequest;
import com.sagar.jewellery.model.embedded.Address;
import com.sagar.jewellery.security.CustomUserDetails;
import com.sagar.jewellery.service.UserService;
import jakarta.validation.Valid;
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
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(userService.getProfile(userDetails.getId()));
    }

    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(userService.updateProfile(userDetails.getId(), request));
    }

    @PutMapping("/address/permanent")
    public ResponseEntity<UserProfileResponse> updatePermanentAddress(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody Address address) {
        return ResponseEntity.ok(userService.updatePermanentAddress(userDetails.getId(), address));
    }

    @PutMapping("/address/live")
    public ResponseEntity<UserProfileResponse> updateLiveAddress(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody Address address) {
        return ResponseEntity.ok(userService.updateLiveAddress(userDetails.getId(), address));
    }

    @PostMapping("/gallery/upload")
    public ResponseEntity<String> uploadGalleryImage(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("file") MultipartFile file) {
        String fileUrl = userService.uploadGalleryImage(userDetails.getId(), file);
        return ResponseEntity.ok(fileUrl);
    }

    @GetMapping("/gallery")
    public ResponseEntity<List<String>> getGalleryImages(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(userService.getGalleryImages(userDetails.getId()));
    }

    @GetMapping("/gallery/file/{filename:.+}")
    public ResponseEntity<Resource> getGalleryFile(@PathVariable String filename) {
        try {
            Path filePath = Paths.get("uploads/gallery").resolve(filename).normalize();
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
