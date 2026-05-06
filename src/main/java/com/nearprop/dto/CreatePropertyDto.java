package com.nearprop.dto;

import com.nearprop.entity.PropertyLabel;
import com.nearprop.entity.PropertyStatus;
import com.nearprop.entity.PropertyType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePropertyDto {
    @NotBlank(message = "Title is required")
    private String title;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    @NotNull(message = "Property type is required")
    private PropertyType type;
    
    @NotNull(message = "Property status is required")
    private PropertyStatus status;
    
    private PropertyLabel label;
    
    @NotNull(message = "Price is required")
    @Min(value = 1, message = "Price must be greater than 0")
    private BigDecimal price;
    
    @NotNull(message = "Area is required")
    @Min(value = 1, message = "Area must be greater than 0")
    private Double area;
    
    private String sizePostfix;
    
    private Double landArea;
    
    private String landAreaPostfix;
    
    @NotBlank(message = "Address is required")
    private String address;
    
    @NotNull(message = "District ID is required")
    private Long districtId;
    
    private String districtName;
    
    private String city;
    
    private String state;
    
    private String pincode;
    
    private String streetNumber;
    
    private String placeName;
    
    @NotNull(message = "Number of bedrooms is required")
    @Min(value = 0, message = "Bedrooms cannot be negative")
    private Integer bedrooms;
    
    @NotNull(message = "Number of bathrooms is required")
    @Min(value = 0, message = "Bathrooms cannot be negative")
    private Integer bathrooms;
    
    private Integer garages;
    
    private Double garageSize;
    
    private Integer yearBuilt;
    
    private String availability;
    
    private String renovated;
    
    private String videoUrl;
    
    private String youtubeUrl;
    
    private Double latitude;
    private Double longitude;
    
    private String note;
    
    private String privateNote;
    
    private Boolean agreementAccepted;
    
    private Set<String> amenities;
    
    private Set<String> securityFeatures;
    
    private Set<String> luxuriousFeatures;
    
    private Set<String> features;
    
    private List<String> images;
    
    private Map<String, String> additionalDetails;
    
    // Developer specific fields
    private String unitType;
    
    private Integer unitCount;
    
    private Integer stock;
} 