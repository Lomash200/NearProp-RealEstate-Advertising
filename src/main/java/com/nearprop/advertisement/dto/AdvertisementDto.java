package com.nearprop.advertisement.dto;

import com.nearprop.dto.DistrictDto;
import com.nearprop.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdvertisementDto {
    private Long id;
    private String title;
    private String description;
    private String bannerImageUrl;
    private String videoUrl;
    
    // Advertiser information
    private String websiteUrl;
    private String whatsappNumber;
    private String phoneNumber;
    private String emailAddress;
    
    // Social media links
    private String instagramUrl;
    private String facebookUrl;
    private String youtubeUrl;
    private String twitterUrl;
    private String linkedinUrl;
    
    private String additionalInfo;
    private String targetLocation;
    private Double latitude;
    private Double longitude;
    private Double radiusKm;
    
    // District information
    private String districtName;
    private Long districtId;
    private Set<DistrictDto> targetDistricts;
    
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private boolean active;
    
    // Analytics data
    private Long viewCount;
    private Long clickCount;
    private Long whatsappClicks;
    private Long phoneClicks;
    private Long websiteClicks;
    private Long socialMediaClicks;
    
    private UserDto createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 