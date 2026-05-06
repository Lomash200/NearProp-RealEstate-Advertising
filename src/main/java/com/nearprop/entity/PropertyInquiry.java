package com.nearprop.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "property_inquiries")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyInquiry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String mobileNumber;

    @Enumerated(EnumType.STRING)
    private InfoType infoType;

    @Enumerated(EnumType.STRING)
    private PropertyType propertyType;

    private Double maxPrice;
    private Integer bedrooms;
    private Integer bathrooms;
    private String minSize;

    private String state;
    private String city;
    private String area;
    private String zipCode;
    private Long districtId;

    private Double latitude; // optional
    private Double longitude; // optional

    @Column(length = 2000)
    private String message;

    @Enumerated(EnumType.STRING)
    private InquiryStatus status;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime lastUpdatedAt;

    @OneToMany(mappedBy = "inquiry", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PropertyInquiryStatusHistory> statusHistory;

    public enum InfoType { RENT, SELL, PURCHASE, OTHER }
    public enum InquiryStatus { IN_REVIEW, COMPLETED, OTHER }
    // PropertyType enum will be imported from existing code
} 