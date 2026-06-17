package com.sagar.jewellery.service;

import com.sagar.jewellery.dto.AdminDashboardResponse;
import com.sagar.jewellery.dto.MakerProfileResponse;
import com.sagar.jewellery.dto.UserProfileResponse;

import java.util.List;

public interface AdminService {
    List<MakerProfileResponse> getAllMakers();
    MakerProfileResponse approveMaker(String makerId);
    MakerProfileResponse revokeMaker(String makerId);
    List<UserProfileResponse> getAllUsers();
    void deleteUser(String userId);
    AdminDashboardResponse getDashboardSummary();
}
