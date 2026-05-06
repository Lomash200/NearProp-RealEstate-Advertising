package com.nearprop.advertisement.entity;

import com.nearprop.entity.District;
import com.nearprop.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "advertisements")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Advertisement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false, length = 1000)
    private String description;
    
    @Column(nullable = false)
    private String bannerImageUrl;
    
    // Video content
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
    
    // Additional information
    @Column(length = 1000)
    private String additionalInfo;
    
    @Column(nullable = false)
    private String targetLocation;
    
    // Geographical coordinates for targeted location
    private Double latitude;
    private Double longitude;
    
    // Target radius in kilometers
    @Column(nullable = false)
    private Double radiusKm;
    
    // Primary district
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id")
    private District district;
    
    @Column(name = "district_name")
    private String districtName;
    
    // Multiple districts targeting
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "advertisement_districts",
        joinColumns = @JoinColumn(name = "advertisement_id"),
        inverseJoinColumns = @JoinColumn(name = "district_id")
    )
    @Builder.Default
    private Set<District> targetDistricts = new HashSet<>();
    
    // Date range for advertisement validity
    @Column(nullable = false)
    private LocalDateTime validFrom;
    
    @Column(nullable = false)
    private LocalDateTime validUntil;
    
    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;
    
    // Analytics data
    @Column(name = "view_count", nullable = false)
    @Builder.Default
    private Long viewCount = 0L;
    
    @Column(name = "click_count", nullable = false)
    @Builder.Default
    private Long clickCount = 0L;
    
    @Column(name = "whatsapp_clicks", nullable = false)
    @Builder.Default
    private Long whatsappClicks = 0L;
    
    @Column(name = "phone_clicks", nullable = false)
    @Builder.Default
    private Long phoneClicks = 0L;
    
    @Column(name = "website_clicks", nullable = false)
    @Builder.Default
    private Long websiteClicks = 0L;
    
    @Column(name = "social_media_clicks", nullable = false)
    @Builder.Default
    private Long socialMediaClicks = 0L;
    
    // Email notification flags
    @Column(name = "day_before_notification_sent")
    private Boolean dayBeforeNotificationSent = false;
    
    @Column(name = "hours_before_notification_sent")
    private Boolean hoursBeforeNotificationSent = false;
    
    @Column(name = "expiry_notification_sent")
    private Boolean expiryNotificationSent = false;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    // Track click events for detailed analytics
    @OneToMany(mappedBy = "advertisement", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AdvertisementClick> clicks = new ArrayList<>();
} 