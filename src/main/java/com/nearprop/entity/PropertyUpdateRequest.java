package com.nearprop.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "property_update_requests")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyUpdateRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String requestId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_by", nullable = false)
    private User requestedBy;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by_admin")
    private User reviewedByAdmin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by_franchisee")
    private User reviewedByFranchisee;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;
    
    @Column(name = "request_notes", length = 1000)
    private String requestNotes;
    
    @Column(name = "admin_notes", length = 1000)
    private String adminNotes;
    
    @Column(name = "franchisee_notes", length = 1000)
    private String franchiseeNotes;
    
    @Column(name = "rejection_reason", length = 1000)
    private String rejectionReason;
    
    @Column(name = "district")
    private String district;
    
    @Column(name = "admin_reviewed")
    private boolean adminReviewed;
    
    @Column(name = "franchisee_reviewed")
    private boolean franchiseeReviewed;
    
    @Column(name = "admin_approved")
    private Boolean adminApproved;
    
    @Column(name = "franchisee_approved")
    private Boolean franchiseeApproved;
    
    @ElementCollection
    @CollectionTable(name = "property_update_fields", 
                    joinColumns = @JoinColumn(name = "update_request_id"))
    @MapKeyColumn(name = "field_name")
    @Column(name = "old_value", columnDefinition = "TEXT")
    @Builder.Default
    private Map<String, String> oldValues = new HashMap<>();
    
    @ElementCollection
    @CollectionTable(name = "property_update_new_fields", 
                    joinColumns = @JoinColumn(name = "update_request_id"))
    @MapKeyColumn(name = "field_name")
    @Column(name = "new_value", columnDefinition = "TEXT")
    @Builder.Default
    private Map<String, String> newValues = new HashMap<>();
    
    @Column(name = "submitted_at")
    @CreationTimestamp
    private LocalDateTime submittedAt;
    
    @Column(name = "admin_reviewed_at")
    private LocalDateTime adminReviewedAt;
    
    @Column(name = "franchisee_reviewed_at")
    private LocalDateTime franchiseeReviewedAt;
    
    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    @Column(name = "is_franchisee_request")
    private boolean franchiseeRequest;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "franchisee_id")
    private User franchisee;
    

    public enum RequestStatus {
        PENDING,
        APPROVED,
        REJECTED,
        CANCELLED
    }
} 
