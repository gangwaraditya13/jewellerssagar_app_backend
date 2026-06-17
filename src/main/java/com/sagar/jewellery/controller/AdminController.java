package com.sagar.jewellery.controller;

import com.sagar.jewellery.dto.AdminDashboardResponse;
import com.sagar.jewellery.dto.MakerProfileResponse;
import com.sagar.jewellery.dto.UserProfileResponse;
import com.sagar.jewellery.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/makers")
    public ResponseEntity<List<MakerProfileResponse>> getAllMakers() {
        return ResponseEntity.ok(adminService.getAllMakers());
    }

    @PutMapping("/makers/{makerId}/approve")
    public ResponseEntity<MakerProfileResponse> approveMaker(@PathVariable String makerId) {
        return ResponseEntity.ok(adminService.approveMaker(makerId));
    }

    @PutMapping("/makers/{makerId}/revoke")
    public ResponseEntity<MakerProfileResponse> revokeMaker(@PathVariable String makerId) {
        return ResponseEntity.ok(adminService.revokeMaker(makerId));
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserProfileResponse>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
        adminService.deleteUser(userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardResponse> getDashboardSummary() {
        return ResponseEntity.ok(adminService.getDashboardSummary());
    }
}
