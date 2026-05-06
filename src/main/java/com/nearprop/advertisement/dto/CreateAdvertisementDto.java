package com.nearprop.advertisement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAdvertisementDto {
    
    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title cannot exceed 100 characters")
    private String title;
    
    @NotBlank(message = "Description is required")
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;
    
    // Banner image can be provided as URL or uploaded file
    private String bannerImageUrl;
    
    // Allow for image upload
    private MultipartFile bannerImage;
    
    // Video content (optional)
    private String videoUrl;
    private MultipartFile videoFile;
    
    // Advertiser information
    private String websiteUrl;
    
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "WhatsApp number must be a valid phone number")
    private String whatsappNumber;
    
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number must be a valid phone number")
    private String phoneNumber;
    
    @Email(message = "Email address must be valid")
    private String emailAddress;
    
    // Social media links
    private String instagramUrl;
    private String facebookUrl;
    private String youtubeUrl;
    private String twitterUrl;
    private String linkedinUrl;
    
    // Additional information
    @Size(max = 1000, message = "Additional information cannot exceed 1000 characters")
    private String additionalInfo;
    
    @NotBlank(message = "Target location is required")
    private String targetLocation;
    
    private Double latitude;
    
    private Double longitude;
    
    @NotNull(message = "Radius (in km) is required")
    private Double radiusKm;
    
    // Primary district
    private String districtName;
    private Long districtId;
    
    // Multiple districts targeting
    @Builder.Default
    private Set<Long> targetDistrictIds = new HashSet<>();
    
    @NotNull(message = "Valid from date is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime validFrom;
    
    @NotNull(message = "Valid until date is required")
    @Future(message = "Valid until date must be in the future")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime validUntil;
} 