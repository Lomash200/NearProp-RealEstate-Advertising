package com.nearprop.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nearprop.entity.PropertyLabel;
import com.nearprop.entity.PropertyStatus;
import com.nearprop.entity.PropertyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PropertyDto {
    private Long id;
    private String permanentId;
    private String title;
    private String description;
    private PropertyType type;
    private PropertyStatus status;
    private PropertyLabel label;
    private BigDecimal price;
    private Double area;
    private String sizePostfix;
    private Double landArea;
    private String landAreaPostfix;
    private String address;
    private Long districtId;
    private String districtName;
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
    private String videoUrl;
    private String youtubeUrl;
    private Double latitude;
    private Double longitude;
    private String note;
    private String privateNote;
    private Boolean agreementAccepted;
    private Boolean approved;
    private Boolean featured;
    private Boolean active;
    private Set<String> amenities;
    private Set<String> securityFeatures;
    private Set<String> luxuriousFeatures;
    private Set<String> features;
    private List<String> imageUrls;
    private Map<String, String> additionalDetails;
    private UserSummaryDto owner;
    private String ownerPermanentId;
    private UserSummaryDto addedByUser;
    private Boolean addedByFranchisee;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime subscriptionExpiry;
    private LocalDateTime scheduledDeletion;
    private Long subscriptionId;
    private Long viewCount;
    private Integer favoriteCount;
    private Boolean isFavorite;
    private Set<String> tags;
    private Float rating;
    private Integer reviewCount;
    private Integer reelCount;
    private Boolean requiresSpecialAccess;
    
    // Developer-specific fields
    private String unitType;
    private Integer unitCount;
    private Integer stock;

    // Distance from user in km
    private Double distanceKm;

    // Google Maps route link from user location to property
    private String routeLink;
    
    private String subscriptionPlanName;

} 
