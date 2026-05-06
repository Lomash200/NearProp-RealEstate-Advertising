package com.nearprop.repository;

import com.nearprop.entity.PaymentTransaction;
import com.nearprop.entity.PaymentTransaction.PaymentStatus;
import com.nearprop.entity.PaymentTransaction.PaymentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {

    Optional<PaymentTransaction> findByReferenceId(String referenceId);
    
    Optional<PaymentTransaction> findByGatewayTransactionId(String gatewayTransactionId);
    
    Optional<PaymentTransaction> findByGatewayOrderId(String gatewayOrderId);
    
    List<PaymentTransaction> findByUserId(Long userId);
    
    Page<PaymentTransaction> findByUserId(Long userId, Pageable pageable);
    
    List<PaymentTransaction> findByUserIdAndStatus(Long userId, PaymentStatus status);
    
    List<PaymentTransaction> findByUserIdAndPaymentType(Long userId, PaymentType paymentType);
    
    List<PaymentTransaction> findBySubscriptionId(Long subscriptionId);
    
    @Query("SELECT p FROM PaymentTransaction p WHERE p.status = :status AND p.createdAt < :cutoffTime")
    List<PaymentTransaction> findStalePaymentsByStatus(PaymentStatus status, LocalDateTime cutoffTime);
    
    @Query("SELECT COUNT(p) FROM PaymentTransaction p WHERE p.user.id = :userId AND p.status = :status")
    long countByUserIdAndStatus(Long userId, PaymentStatus status);
    
    @Query("SELECT SUM(p.amount) FROM PaymentTransaction p WHERE p.user.id = :userId AND p.status = :status")
    Double sumAmountByUserIdAndStatus(Long userId, PaymentStatus status);
} 