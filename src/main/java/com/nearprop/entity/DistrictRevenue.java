package com.nearprop.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "district_revenues")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DistrictRevenue {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "district_id", nullable = false)
    private Long districtId;
    
    @Column(name = "district_name", nullable = false)
    private String districtName;
    
    @Column(name = "state", nullable = false)
    private String state;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "franchisee_district_id", nullable = false)
    private FranchiseeDistrict franchiseeDistrict;
    
    // The transaction that generated revenue (property listing, subscription, etc.)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RevenueType revenueType;
    
    // References to related entities based on revenue type
    @Column(name = "property_id")
    private Long propertyId;
    
    @Column(name = "subscription_id")
    private Long subscriptionId;
    
    @Column(name = "transaction_id")
    private String transactionId;
    
    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;
    
    // Total revenue amount
    @Column(nullable = false)
    private BigDecimal amount;
    
    // Franchisee commission (typically 60% of revenue)
    @Column(name = "franchisee_commission", nullable = false)
    private BigDecimal franchiseeCommission;
    
    // Company revenue (typically 40% of revenue)
    @Column(name = "company_revenue", nullable = false)
    private BigDecimal companyRevenue;
    
    // Payment status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;
    
    // Payment date to franchisee
    @Column(name = "payment_date")
    private LocalDateTime paymentDate;
    
    // Payment reference (e.g., bank transaction ID)
    @Column(name = "payment_reference")
    private String paymentReference;
    
    // Description of the revenue source
    @Column(length = 500)
    private String description;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    public enum RevenueType {
        PROPERTY_LISTING,
        SUBSCRIPTION_PAYMENT,
        VISIT_BOOKING,
        TRANSACTION_FEE,
        MARKETING_FEE,
        WITHDRAWAL,
        OTHER
    }
    
    public enum PaymentStatus {
        PENDING,
        PAID,
        CANCELLED
    }
} 