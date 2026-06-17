package com.sagar.jewellery.service.impl;

import com.sagar.jewellery.dto.UserProfileResponse;
import com.sagar.jewellery.dto.UserUpdateRequest;
import com.sagar.jewellery.exception.ResourceNotFoundException;
import com.sagar.jewellery.mapper.UserMapper;
import com.sagar.jewellery.model.User;
import com.sagar.jewellery.model.embedded.Address;
import com.sagar.jewellery.repository.UserRepository;
import com.sagar.jewellery.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    private final Path rootPath = Paths.get("uploads/gallery");

    public UserServiceImpl() {
        try {
            Files.createDirectories(rootPath);
        } catch (IOException e) {
            // Root path creation error logged or handled
        }
    }

    @Override
    public UserProfileResponse getProfile(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return userMapper.toProfileResponse(user);
    }

    @Override
    public UserProfileResponse updateProfile(String userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        user.setUpdatedAt(LocalDateTime.now());
        User saved = userRepository.save(user);
        return userMapper.toProfileResponse(saved);
    }

    @Override
    public UserProfileResponse updatePermanentAddress(String userId, Address address) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        user.setAddressPermanent(address);
        user.setUpdatedAt(LocalDateTime.now());
        User saved = userRepository.save(user);
        return userMapper.toProfileResponse(saved);
    }

    @Override
    public UserProfileResponse updateLiveAddress(String userId, Address address) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        user.setAddressLive(address);
        user.setUpdatedAt(LocalDateTime.now());
        User saved = userRepository.save(user);
        return userMapper.toProfileResponse(saved);
    }

    @Override
    public String uploadGalleryImage(String userId, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        try {
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path targetLocation = rootPath.resolve(filename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            
            String fileUrl = "/api/user/gallery/file/" + filename;
            if (user.getGallery() == null) {
                user.setGallery(new ArrayList<>());
            }
            user.getGallery().add(fileUrl);
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
            return fileUrl;
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file. Please try again!", ex);
        }
    }

    @Override
    public List<String> getGalleryImages(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return user.getGallery() != null ? user.getGallery() : new ArrayList<>();
    }
}
