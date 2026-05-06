package com.nearprop.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "franchisee_bank_details")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FranchiseeBankDetail {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // User with ROLE_FRANCHISEE
    
    @Column(name = "account_name", nullable = false)
    private String accountName;
    
    @Column(name = "account_number", nullable = false)
    private String accountNumber;
    
    @Column(name = "ifsc_code", nullable = false)
    private String ifscCode;
    
    @Column(name = "bank_name", nullable = false)
    private String bankName;
    
    @Column(name = "branch_name")
    private String branchName;
    
    @Column(name = "account_type")
    private String accountType;
    
    @Column(name = "upi_id")
    private String upiId;
    
    @Column(name = "is_primary", nullable = false)
    private boolean isPrimary;
    
    @Column(name = "is_verified", nullable = false)
    private boolean isVerified;
    
    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;
    
    @Column(name = "verified_by")
    private Long verifiedBy;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
