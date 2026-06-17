package com.sagar.jewellery.model;

import com.sagar.jewellery.model.embedded.Address;
import com.sagar.jewellery.model.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {
    @Id
    private String id;
    @Indexed(unique = true)
    private String email;
    private String password;
    private UserRole role;
    private String fullName;
    private String phone;
    private Address addressPermanent;
    private Address addressLive;
    @Builder.Default
    private List<String> gallery = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
