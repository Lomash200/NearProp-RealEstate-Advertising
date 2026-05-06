package com.nearprop.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nearprop.entity.Reel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReelDto {
    
    private Long id;
    
    @NotNull(message = "Property ID is required")
    private Long propertyId;
    
    private PropertyDto property;
    
    private UserDto owner;
    
    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;
    
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
    
    private String videoUrl;
    
    private String thumbnailUrl;
    
    private Integer durationSeconds;
    
    private Long fileSize;
    
    private Reel.ReelStatus status;
    
    private Reel.ProcessingStatus processingStatus;
    
    private String publicId;
    
//    // Payment related fields
//    private Boolean paymentRequired;
//    private String paymentTransactionId;
//
    private Long viewCount;
    
    private Long likeCount;
    
    private Long commentCount;
    
    private Long shareCount;
    
    private Long saveCount;
    
    // Location data
    private Double latitude;
    private Double longitude;
    private String district; 
    private String city;
    private String state;
    
    // Distance from user in km (used for nearby reels)
    private Double distanceKm;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    // Flag to indicate if current user liked this reel
    private Boolean liked;
    
    // Flag to indicate if current user follows this reel
    private Boolean followed;
    
    // Flag to indicate if current user saved this reel
    private Boolean saved;
    
    // List of comments on this reel
    private List<ReelInteractionDto> comments;

    // List of user IDs who liked this reel
    private List<Long> likedBy;
} 