package com.nearprop.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_transactions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "reference_id", nullable = false, unique = true)
    private String referenceId;
    
    @Column(name = "gateway_transaction_id")
    private String gatewayTransactionId;
    
    @Column(name = "payment_id")
    private String paymentId;
    
    @Column(name = "gateway_order_id")
    private String gatewayOrderId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private BigDecimal amount;
    
    @Column(name = "original_amount")
    private BigDecimal originalAmount;
    
    @Column(nullable = false)
    private String currency;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", nullable = false)
    private PaymentType paymentType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private PaymentMethod paymentMethod;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;
    
    @Column(name = "failure_message")
    private String failureMessage;
    
    @Column(name = "failure_code")
    private String failureCode;
    
    @Column(name = "subscription_id")
    private Long subscriptionId;
    
    @Column(name = "property_id")
    private Long propertyId;
    
    @Column(name = "coupon_code")
    private String couponCode;
    
    @Column(name = "discount_details")
    private String discountDetails;
    
    @Column(name = "receipt_url")
    private String receiptUrl;
    
    @Column(name = "refund_reference_id")
    private String refundReferenceId;
    
    @Column(name = "refund_amount")
    private BigDecimal refundAmount;
    
    @Column(name = "refund_reason")
    private String refundReason;
    
    @Column(name = "refund_status")
    private String refundStatus;
    
    @Column(name = "gateway_response", columnDefinition = "TEXT")
    private String gatewayResponse;
    
    @Column(name = "ip_address")
    private String ipAddress;
    
    @Column(name = "user_agent")
    private String userAgent;
    
    @Column(name = "payment_date")
    private LocalDateTime paymentDate;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    /**
     * Payment status enum representing different states of a payment
     */
    public enum PaymentStatus {
        INITIATED, 
        PROCESSING, 
        COMPLETED,
        FAILED, 
        CANCELLED, 
        REFUNDED,
        PARTIALLY_REFUNDED
    }
    
    /**
     * Payment types for different payment purposes
     */
    public enum PaymentType {
        SUBSCRIPTION,
        PROPERTY_LISTING,
        FRANCHISE_FEE,
        SERVICE_FEE,
        REEL_PURCHASE,
        OTHER
    }
    
    /**
     * Payment methods supported by the system
     */
    public enum PaymentMethod {
        CREDIT_CARD,
        DEBIT_CARD,
        NET_BANKING,
        UPI,
        WALLET,
        BANK_TRANSFER,
        EMI,
        OTHER
    }
    
    /**
     * Check if payment is successful/complete
     * @return true if payment status is COMPLETED
     */
    @Transient
    public boolean isSuccessful() {
        return this.status == PaymentStatus.COMPLETED;
    }
    
    /**
     * Check if payment is refunded
     * @return true if payment status is REFUNDED or PARTIALLY_REFUNDED
     */
    @Transient
    public boolean isRefunded() {
        return this.status == PaymentStatus.REFUNDED || this.status == PaymentStatus.PARTIALLY_REFUNDED;
    }
    
    /**
     * Check if payment can be refunded
     * @return true if payment is successful and not already refunded
     */
    @Transient
    public boolean canBeRefunded() {
        return isSuccessful() && !isRefunded();
    }
} 