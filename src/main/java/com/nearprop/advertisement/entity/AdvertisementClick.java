package com.nearprop.advertisement.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "advertisement_clicks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdvertisementClick {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "advertisement_id", nullable = false)
    private Advertisement advertisement;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ClickType clickType;
    
    // IP address of the user (anonymized if needed)
    private String ipAddress;
    
    // User agent information
    @Column(length = 500)
    private String userAgent;
    
    // User ID if available (for logged-in users)
    private Long userId;
    
    // District of the user if available
    private String userDistrict;
    
    // Referrer URL
    @Column(length = 500)
    private String referrer;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    // Enum for click types
    public enum ClickType {
        VIEW,
        WEBSITE,
        WHATSAPP,
        PHONE,
        INSTAGRAM,
        FACEBOOK,
        YOUTUBE,
        TWITTER,
        LINKEDIN,
        OTHER
    }
} 