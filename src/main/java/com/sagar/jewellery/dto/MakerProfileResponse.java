package com.sagar.jewellery.dto;

import com.sagar.jewellery.model.embedded.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MakerProfileResponse {
    private String id;
    private String email;
    private String role;
    private String fullName;
    private String phone;
    private boolean isApproved;
    private String shopName;
    private Address address;
}
