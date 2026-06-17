package com.sagar.jewellery.model.embedded;

import com.sagar.jewellery.model.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatusHistory {
    private OrderStatus status;
    private String updatedBy;
    private LocalDateTime updatedAt;
    private String comment;
}
