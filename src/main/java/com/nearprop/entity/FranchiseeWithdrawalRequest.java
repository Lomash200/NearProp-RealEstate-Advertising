package com.nearprop.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "franchisee_withdrawal_requests")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FranchiseeWithdrawalRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "franchisee_district_id", nullable = false)
    private FranchiseeDistrict franchiseeDistrict;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_detail_id")
    private FranchiseeBankDetail bankDetail;
    
    @Column(nullable = false)
    private BigDecimal requestedAmount;
    
    @Column(length = 1000, nullable = false)
    private String reason;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WithdrawalStatus status;
    
    @Column(name = "processed_by")
    private Long processedById;
    
    @Column(name = "processed_at")
    private LocalDateTime processedAt;
    
    @Column(name = "admin_comments", length = 1000)
    private String adminComments;
    
    @Column(name = "payment_reference")
    private String paymentReference;
    
    @Column(name = "payment_date")
    private LocalDateTime paymentDate;
    
    @Column(name = "screenshot_url", length = 1000)
    private String screenshotUrl;
    
    @Column(name = "account_number")
    private String accountNumber;
    
    @Column(name = "ifsc_code")
    private String ifscCode;
    
    @Column(name = "bank_name")
    private String bankName;
    
    @Column(name = "mobile_number")
    private String mobileNumber;
    
    // Additional fields for transaction details
    @Column(name = "transaction_type")
    private String transactionType;
    
    @Column(name = "transaction_id")
    private String transactionId;
    
    // Balance tracking fields
    @Column(name = "original_balance")
    private BigDecimal originalBalance;
    
    @Column(name = "updated_balance")
    private BigDecimal updatedBalance;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    public enum WithdrawalStatus {
        PENDING,
        APPROVED,
        PAID,
        REJECTED
    }
} 