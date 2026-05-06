package com.nearprop.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "franchisee_districts", 
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_franchisee_district", columnNames = {"user_id", "district_id"})
       })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FranchiseeDistrict {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // User with ROLE_FRANCHISEE
    
    @Column(name = "district_id", nullable = false)
    private Long districtId;
    
    @Column(name = "district_name", nullable = false)
    private String districtName;
    
    @Column(name = "state", nullable = false)
    private String state;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "franchise_request_id")
    private FranchiseRequest franchiseRequest;
    
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;
    
    @Column(name = "end_date")
    private LocalDateTime endDate;
    
    @Column(nullable = false)
    private boolean active;
    
    // Revenue share attributes
    @Column(name = "revenue_share_percentage", nullable = false)
    private BigDecimal revenueSharePercentage;  // Default 60%
    
    // Performance metrics
    @Column(name = "total_properties")
    private Integer totalProperties;
    
    @Column(name = "total_transactions")
    private Integer totalTransactions;
    
    @Column(name = "total_revenue")
    private BigDecimal totalRevenue;
    
    @Column(name = "total_commission")
    private BigDecimal totalCommission;
    
    // Balance tracking
    @Column(name = "available_balance")
    private BigDecimal availableBalance;
    
    // Store withdrawal history as JSON text
    @Column(name = "withdrawal_history", columnDefinition = "text")
    @Builder.Default
    private String withdrawalHistory = "[]";
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FranchiseeStatus status;
    
    // Contact and office details
    @Column(name = "office_address")
    private String officeAddress;
    
    @Column(name = "contact_phone")
    private String contactPhone;
    
    @Column(name = "contact_email")
    private String contactEmail;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    public enum FranchiseeStatus {
        ACTIVE,
        SUSPENDED,
        TERMINATED,
        PENDING_APPROVAL
    }
} 