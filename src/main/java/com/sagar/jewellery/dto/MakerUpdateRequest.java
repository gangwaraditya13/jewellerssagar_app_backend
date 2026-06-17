package com.sagar.jewellery.dto;

import com.sagar.jewellery.model.embedded.Address;
import lombok.Data;

@Data
public class MakerUpdateRequest {
    private String fullName;
    private String phone;
    private String shopName;
    private Address address;
}
