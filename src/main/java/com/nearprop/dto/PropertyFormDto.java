package com.nearprop.dto;

import com.nearprop.entity.PropertyLabel;
import com.nearprop.entity.PropertyStatus;
import com.nearprop.entity.PropertyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * DTO for handling property form data with file uploads
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyFormDto {
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
    @Positive(message = "Price must be positive")
    private BigDecimal price;
    
    @Positive(message = "Area must be positive")
    private Double area;
    
    private String sizePostfix;
    
    private Double landArea;
    
    private String landAreaPostfix;
    
    @NotBlank(message = "Address is required")
    private String address;
    
    @NotNull(message = "District ID is required")
    private Long districtId;
    
    private String city;
    
    private String state;
    
    private String pincode;
    
    private String streetNumber;
    
    private String placeName;
  
    private Integer bedrooms;
    
    private Integer bathrooms;
    
    private Integer garages;
    
    private Double garageSize;
    
    private Integer yearBuilt;
    
    private String availability;
    
    private String renovated;
    
    // URLs will be generated after file upload
    // private String videoUrl;
    // private String youtubeUrl;
    
    private Double latitude;
    
    private Double longitude;
    
    private String note;
    
    private String privateNote;
    
    private Boolean agreementAccepted;
    
    private String amenities; // Will be parsed from JSON string
    
    private String securityFeatures; // Will be parsed from JSON string
    
    private String luxuriousFeatures; // Will be parsed from JSON string
    
    private String features; // Will be parsed from JSON string
    
    // Image URLs will be generated after file upload
    // private List<String> images;
    
    private String additionalDetails; // Will be parsed from JSON string
    
    // Convert to CreatePropertyDto
    public CreatePropertyDto toCreatePropertyDto(List<String> imageUrls, String videoUrl, String youtubeUrl) {
        return CreatePropertyDto.builder()
                .title(title)
                .description(description)
                .type(type)
                .status(status)
                .label(label)
                .price(price)
                .area(area)
                .sizePostfix(sizePostfix)
                .landArea(landArea)
                .landAreaPostfix(landAreaPostfix)
                .address(address)
                .districtId(districtId)
                .city(city)
                .state(state)
                .pincode(pincode)
                .streetNumber(streetNumber)
                .placeName(placeName)
                .bedrooms(bedrooms)
                .bathrooms(bathrooms)
                .garages(garages)
                .garageSize(garageSize)
                .yearBuilt(yearBuilt)
                .availability(availability)
                .renovated(renovated)
                .videoUrl(videoUrl)
                .youtubeUrl(youtubeUrl)
                .latitude(latitude)
                .longitude(longitude)
                .note(note)
                .privateNote(privateNote)
                .agreementAccepted(agreementAccepted)
                // These fields will need to be parsed from JSON strings in the service
                // .amenities(amenities)
                // .securityFeatures(securityFeatures)
                // .luxuriousFeatures(luxuriousFeatures)
                // .features(features)
                .images(imageUrls)
                // .additionalDetails(additionalDetails)
                .build();
    }
} 
