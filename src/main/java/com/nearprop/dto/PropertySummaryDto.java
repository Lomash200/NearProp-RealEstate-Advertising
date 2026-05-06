package com.nearprop.dto;

import com.nearprop.entity.PropertyStatus;
import com.nearprop.entity.PropertyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertySummaryDto {
    private Long id;
    private String title;
    private String address;
    private String district;
    private PropertyType type;
    private PropertyStatus status;
    private BigDecimal price;
    private String thumbnail;
} 