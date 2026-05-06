package com.nearprop.dto;

import com.nearprop.entity.PropertyLabel;
import com.nearprop.entity.PropertyStatus;
import com.nearprop.entity.PropertyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * DTO for handling developer property form data with additional unit information
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class DeveloperPropertyFormDto extends PropertyFormDto {
    
    @NotBlank(message = "Unit type is required for developer properties")
    private String unitType;
    
    @NotNull(message = "Unit count is required for developer properties")
    @Positive(message = "Unit count must be positive")
    private Integer unitCount;
    
    @NotNull(message = "Stock is required for developer properties")
    @Positive(message = "Stock must be positive")
    private Integer stock;
    
    // Override to include developer-specific fields
    @Override
    public CreatePropertyDto toCreatePropertyDto(List<String> imageUrls, String videoUrl, String youtubeUrl) {
        CreatePropertyDto baseDto = super.toCreatePropertyDto(imageUrls, videoUrl, youtubeUrl);
        
        // Add developer-specific fields
        baseDto.setUnitType(unitType);
        baseDto.setUnitCount(unitCount);
        baseDto.setStock(stock);
        
        return baseDto;
    }
} 