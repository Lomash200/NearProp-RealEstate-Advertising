package com.nearprop.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "monthly_revenue_reports")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyRevenueReport {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Report metadata
    @Column(nullable = false)
    private Integer year;
    
    @Column(nullable = false)
    private Integer month;
    
    @CreationTimestamp
    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "report_status", nullable = false)
    private ReportStatus reportStatus;
    
    // Franchisee details
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "franchisee_id", nullable = false)
    private User franchisee;
    
    @Column(name = "franchisee_name", nullable = false)
    private String franchiseeName;
    
    @Column(name = "business_name")
    private String businessName;
    
    // District details
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "franchisee_district_id", nullable = false)
    private FranchiseeDistrict franchiseeDistrict;
    
    @Column(name = "district_id", nullable = false)
    private Long districtId;
    
    @Column(name = "district_name", nullable = false)
    private String districtName;
    
    @Column(nullable = false)
    private String state;
    
    // Revenue details
    @Column(name = "total_revenue", nullable = false)
    private BigDecimal totalRevenue;
    
    @Column(name = "franchisee_commission", nullable = false)
    private BigDecimal franchiseeCommission;
    
    @Column(name = "admin_share", nullable = false)
    private BigDecimal adminShare;
    
    @Column(name = "total_subscriptions")
    private Integer totalSubscriptions;
    
    @Column(name = "new_subscriptions")
    private Integer newSubscriptions;
    
    @Column(name = "renewed_subscriptions")
    private Integer renewedSubscriptions;
    
    // Payment details
    @Column(name = "payment_due_date")
    private LocalDate paymentDueDate;
    
    @Column(name = "payment_date")
    private LocalDate paymentDate;
    
    @Column(name = "payment_reference")
    private String paymentReference;
    
    @Column(name = "payment_method")
    private String paymentMethod;
    
    @Column(name = "admin_comments", length = 1000)
    private String adminComments;
    
    // Bank details
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_detail_id")
    private FranchiseeBankDetail bankDetail;
    
    @Column(name = "account_name")
    private String accountName;
    
    @Column(name = "account_number")
    private String accountNumber;
    
    @Column(name = "bank_name")
    private String bankName;
    
    @Column(name = "ifsc_code")
    private String ifscCode;
    
    // Withdrawal details
    @Column(name = "emergency_withdrawals_amount")
    private BigDecimal emergencyWithdrawalsAmount;
    
    @Column(name = "emergency_withdrawals_count")
    private Integer emergencyWithdrawalsCount;
    
    // Balance calculation
    @Column(name = "previous_balance")
    private BigDecimal previousBalance;
    
    @Column(name = "current_balance")
    private BigDecimal currentBalance;
    
    @Column(name = "final_payable_amount")
    private BigDecimal finalPayableAmount;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "processed_by")
    private Long processedById;
    
    @Column(name = "processed_at")
    private LocalDateTime processedAt;
    
    // Report status enum
    public enum ReportStatus {
        PENDING,
        PAID,
        CANCELLED
    }
} 