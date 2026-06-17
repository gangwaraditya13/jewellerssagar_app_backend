package com.sagar.jewellery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Model3DResponse {
    private String id;
    private String productId;
    private String fileUrl;
    private long fileSize;
    private String uploadedBy;
    private LocalDateTime uploadedAt;
}
