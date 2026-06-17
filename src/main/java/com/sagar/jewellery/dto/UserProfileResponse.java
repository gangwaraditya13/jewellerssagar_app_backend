package com.sagar.jewellery.dto;

import com.sagar.jewellery.model.embedded.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private String id;
    private String email;
    private String role;
    private String fullName;
    private String phone;
    private Address addressPermanent;
    private Address addressLive;
    private List<String> gallery;
}
