package com.sagar.jewellery.service;

import com.sagar.jewellery.dto.MakerProfileResponse;
import com.sagar.jewellery.dto.MakerUpdateRequest;

public interface MakerService {
    MakerProfileResponse getProfile(String makerId);
    MakerProfileResponse updateProfile(String makerId, MakerUpdateRequest request);
    double getEarningsSummary(String makerId);
}
