package com.nearprop.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePropertyUpdateRequestDto {
    
    @NotNull(message = "Property ID is required")
    private Long propertyId;
    
    private String requestNotes;
    
    // Fields that can be updated
    private String title;
    private String description;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private String streetNumber;
    private String placeName;
    private Double area;
    private String sizePostfix;
    private Double landArea;
    private String landAreaPostfix;
    private Integer bedrooms;
    private Integer bathrooms;
    private Integer garages;
    private Double garageSize;
    private Integer yearBuilt;
    private String availability;
    private String renovated;
    private String note;
    private String privateNote;
    private Double latitude;
    private Double longitude;
    
    // Lists stored as JSON strings
    private String amenities;
    private String securityFeatures;
    private String luxuriousFeatures;
    private String features;
    private String additionalDetails;
    
    // For franchisee requests
    private String ownerPermanentId;
} 