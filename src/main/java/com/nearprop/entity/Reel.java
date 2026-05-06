//package com.nearprop.entity;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import org.hibernate.annotations.CreationTimestamp;
//import org.hibernate.annotations.UpdateTimestamp;
//import java.util.List;
//import java.util.ArrayList;
//import java.time.LocalDateTime;
//
//@Entity
//@Table(name = "property_reels")
//@Data
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//public class Reel {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "property_id", nullable = false)
//    private Property property;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id", nullable = false)
//    private User owner;
//
//    @Column(nullable = false)
//    private String title;
//
//    @Column(length = 500)
//    private String description;
//
//    @Column(nullable = false)
//    private String videoUrl;
//
//    @Column(nullable = false)
//    private String thumbnailUrl;
//
//    @Column(nullable = false)
//    private Integer durationSeconds;
//
//    @Column(nullable = false)
//    private Long fileSize; // in bytes
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private ReelStatus status;
//
//    @Column(name = "view_count", nullable = false)
//    private Long viewCount;
//
//    @Column(name = "like_count", nullable = false)
//    private Long likeCount;
//
//    @Column(name = "comment_count", nullable = false)
//    private Long commentCount;
//
//    @Column(name = "share_count", nullable = false)
//    private Long shareCount;
//
//    @Column(name = "save_count", nullable = false)
//    private Long saveCount;
//
//    @Column(name = "processing_status", nullable = false)
//    @Enumerated(EnumType.STRING)
//    private ProcessingStatus processingStatus;
//
//    @Column(name = "public_id", nullable = false, unique = true)
//    private String publicId; // Used for public sharing links
//
//    // Payment related fields
//    @Column(name = "payment_required", nullable = false)
//    private Boolean paymentRequired = false;
//
//    @Column(name = "payment_transaction_id")
//    private String paymentTransactionId;
//
//    // Location information (inherited from property)
//    private Double latitude;
//    private Double longitude;
//    private String district;
//    private String city;
//    private String state;
//
//    @CreationTimestamp
//    @Column(name = "created_at", nullable = false, updatable = false)
//    private LocalDateTime createdAt;
//
//    @UpdateTimestamp
//    @Column(name = "updated_at", nullable = false)
//    private LocalDateTime updatedAt;
//
//    public enum ReelStatus {
//        DRAFT, PUBLISHED, ARCHIVED, REJECTED, HIDDEN
//    }
//
//    public enum ProcessingStatus {
//        QUEUED, PROCESSING, COMPLETED, FAILED
//    }
//
//   @OneToMany(mappedBy = "reel", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<ReelInteraction> interactions = new ArrayList<>();
//}


package com.nearprop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "property_reels")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* ===================== RELATIONS ===================== */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    /* ===================== BASIC INFO ===================== */

    @Column(nullable = false)
    private String title;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private String videoUrl;

    @Column(nullable = false)
    private String thumbnailUrl;

    @Column(nullable = false)
    private Integer durationSeconds;

    @Column(nullable = false)
    private Long fileSize; // bytes

    /* ===================== APPROVAL STATUS ===================== */

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReelStatus status;

    /* ===================== COUNTERS ===================== */

    @Column(name = "view_count", nullable = false)
    private Long viewCount = 0L;

    @Column(name = "like_count", nullable = false)
    private Long likeCount = 0L;

    @Column(name = "comment_count", nullable = false)
    private Long commentCount = 0L;

    @Column(name = "share_count", nullable = false)
    private Long shareCount = 0L;

    @Column(name = "save_count", nullable = false)
    private Long saveCount = 0L;

    /* ===================== VIDEO PROCESSING ===================== */

    @Enumerated(EnumType.STRING)
    @Column(name = "processing_status", nullable = false)
    private ProcessingStatus processingStatus;

    /* ===================== PUBLIC / PAYMENT ===================== */

    @Column(name = "public_id", nullable = false, unique = true)
    private String publicId;

    @Column(name = "payment_required", nullable = false)
    private Boolean paymentRequired = false;

    @Column(name = "payment_transaction_id")
    private String paymentTransactionId;


    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    /* ===================== LOCATION (FROM PROPERTY) ===================== */

    private Double latitude;
    private Double longitude;
    private String district;
    private String city;
    private String state;

    /* ===================== AUDIT ===================== */

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /* ===================== ENUMS ===================== */

    /**
     * Approval flow:
     * PENDING  -> user upload
     * APPROVED -> admin approval (VISIBLE)
     * REJECTED -> admin rejected
     * HIDDEN   -> admin manually hidden later
     */
    public enum ReelStatus {
        PUBLISHED,
        PENDING,
        APPROVED,
        REJECTED,
        HIDDEN
    }

    public enum ProcessingStatus {
        QUEUED,
        PROCESSING,
        COMPLETED,
        FAILED
    }

    /* ===================== INTERACTIONS ===================== */

    @OneToMany(mappedBy = "reel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReelInteraction> interactions = new ArrayList<>();
}
