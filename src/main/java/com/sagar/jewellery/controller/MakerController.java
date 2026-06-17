package com.sagar.jewellery.controller;

import com.sagar.jewellery.dto.MakerProfileResponse;
import com.sagar.jewellery.dto.MakerUpdateRequest;
import com.sagar.jewellery.security.CustomUserDetails;
import com.sagar.jewellery.service.MakerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/maker")
public class MakerController {

    @Autowired
    private MakerService makerService;

    @GetMapping("/profile")
    public ResponseEntity<MakerProfileResponse> getProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(makerService.getProfile(userDetails.getId()));
    }

    @PutMapping("/profile")
    public ResponseEntity<MakerProfileResponse> updateProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody MakerUpdateRequest request) {
        return ResponseEntity.ok(makerService.updateProfile(userDetails.getId(), request));
    }

    @GetMapping("/earnings")
    public ResponseEntity<Double> getEarningsSummary(@AuthenticationPrincipal CustomUserDetails userDetails) {
        double earnings = makerService.getEarningsSummary(userDetails.getId());
        return ResponseEntity.ok(earnings);
    }
}
