package com.sagar.jewellery.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "models_3d")
public class Model3D {
    @Id
    private String id;
    private String productId;
    private String fileUrl;
    private long fileSize;
    private String uploadedBy;
    private LocalDateTime uploadedAt;
}
