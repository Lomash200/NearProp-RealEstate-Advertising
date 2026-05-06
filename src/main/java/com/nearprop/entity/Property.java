package com.nearprop.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;

@Entity
@Table(name = "properties")
@Getter
@Setter
@ToString
@EqualsAndHashCode(exclude = {"favoritedBy", "reels"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Property {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String permanentId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PropertyType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PropertyStatus status;
    
    @Enumerated(EnumType.STRING)
    private PropertyLabel label;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Double area;
    
    @Column(name = "size_postfix")
    private String sizePostfix;
    
    @Column(name = "land_area")
    private Double landArea;
    
    @Column(name = "land_area_postfix")
    private String landAreaPostfix;

    @Column(nullable = false)
    private String address;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "district_id")
    private District district;
    
    @Column(name = "district", nullable = false)
    private String districtValue;
    
    @Column(name = "district_name", nullable = false)
    private String districtName;

    private String city;
    
    private String state;
    
    private String pincode;
    
    @Column(name = "street_number")
    private String streetNumber;
    
    @Column(name = "place_name")
    private String placeName;

    @Column(nullable = false)
    private Integer bedrooms;

    @Column(nullable = false)
    private Integer bathrooms;
    
    @Column(name = "garages")
    private Integer garages;
    
    @Column(name = "garage_size")
    private Double garageSize;
    
    @Column(name = "year_built")
    private Integer yearBuilt;
    
    @Column(name = "availability")
    private String availability;
    
    @Column(name = "renovated")
    private String renovated;
    
    @Column(name = "video_url")
    private String videoUrl;
    
    @Column(name = "youtube_url")
    private String youtubeUrl;

    private Double latitude;
    private Double longitude;
    
    @Column(name = "note", length = 1000)
    private String note;
    
    @Column(name = "private_note", length = 1000)
    private String privateNote;
    
    @Column(name = "agreement_accepted")
    private Boolean agreementAccepted;
    
    // Developer specific fields
    @Column(name = "unit_type")
    private String unitType;
    
    @Column(name = "unit_count")
    private Integer unitCount;
    
    @Column(name = "stock")
    private Integer stock;

    @ElementCollection
    @CollectionTable(name = "property_amenities", joinColumns = @JoinColumn(name = "property_id"))
    @Column(name = "amenity")
    @Builder.Default
    private Set<String> amenities = new HashSet<>();
    
    @ElementCollection
    @CollectionTable(name = "property_security_features", joinColumns = @JoinColumn(name = "property_id"))
    @Column(name = "security_feature")
    @Builder.Default
    private Set<String> securityFeatures = new HashSet<>();
    
    @ElementCollection
    @CollectionTable(name = "property_luxurious_features", joinColumns = @JoinColumn(name = "property_id"))
    @Column(name = "luxurious_feature")
    @Builder.Default
    private Set<String> luxuriousFeatures = new HashSet<>();
    
    @ElementCollection
    @CollectionTable(name = "property_features", joinColumns = @JoinColumn(name = "property_id"))
    @Column(name = "feature")
    @Builder.Default
    private Set<String> features = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "property_images", joinColumns = @JoinColumn(name = "property_id"))
    @Column(name = "image_url")
    @OrderColumn(name = "image_order")
    @Builder.Default
    private List<String> images = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "property_additional_details", 
                    joinColumns = @JoinColumn(name = "property_id"))
    @MapKeyColumn(name = "title")
    @Column(name = "value")
    @Builder.Default
    private Map<String, String> additionalDetails = new HashMap<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    @ToString.Exclude
    private User owner;
    
    // Owner's permanent ID for easier lookup
    @Column(name = "owner_permanent_id")
    private String ownerPermanentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "added_by_user_id")
    @ToString.Exclude
    private User addedByUser;
    
    @Column(name = "added_by_franchisee", nullable = false)
    @Builder.Default
    private boolean addedByFranchisee = false;

    @JsonIgnore
    @ManyToMany(mappedBy = "favorites")
    @Builder.Default
    @ToString.Exclude
    private Set<User> favoritedBy = new HashSet<>();

    @Column(nullable = false)
    private boolean featured;

    @Column(nullable = false)
    private boolean approved;
    
    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;
    
    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    private List<Reel> reels = new ArrayList<>();
    
    @Column(name = "subscription_expiry")
    private LocalDateTime subscriptionExpiry;

    @Column(name = "subscription_id")
    private Long subscriptionId;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    @Column(name = "scheduled_deletion")
    private LocalDateTime scheduledDeletion;
  
    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PropertyUpdateRequest> updateRequests = new ArrayList<>();
   
} 
