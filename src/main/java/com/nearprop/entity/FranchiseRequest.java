package com.nearprop.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "franchise_requests", uniqueConstraints = {
    @UniqueConstraint(name = "uk_franchise_request_user_district", columnNames = {"user_id", "district_id"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FranchiseRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "district_id", nullable = false)
    private Long districtId;
    
    @Column(name = "district_name", nullable = false)
    private String districtName;
    
    @Column(name = "state", nullable = false)
    private String state;
    
    @Column(name = "business_name", nullable = false)
    private String businessName;
    
    @Column(name = "business_address", nullable = false)
    private String businessAddress;
    
    @Column(name = "business_registration_number")
    private String businessRegistrationNumber;
    
    @Column(name = "gst_number")
    private String gstNumber;
    
    @Column(name = "pan_number", nullable = false)
    private String panNumber;
    
    @Column(name = "aadhar_number", nullable = false)
    private String aadharNumber;
    
    @Column(name = "contact_email", nullable = false)
    private String contactEmail;
    
    @Column(name = "contact_phone", nullable = false)
    private String contactPhone;
    
    @Column(name = "years_of_experience")
    private Integer yearsOfExperience;
    
    @Column(name = "document_ids", length = 1000)
    private String documentIds; // Comma-separated list of document IDs stored in AWS S3
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;
    
    @Column(name = "admin_comments", length = 1000)
    private String adminComments;
    
    @Column(name = "reviewed_by")
    private Long reviewedBy; // Admin user ID
    
    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;
    
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    public LocalDateTime getApprovedAt() {
        if (status == RequestStatus.APPROVED) {
            return reviewedAt;
        }
        return null;
    }
    
    public enum RequestStatus {
        PENDING,
        APPROVED,
        REJECTED
    }
}