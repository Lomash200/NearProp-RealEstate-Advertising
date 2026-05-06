package com.nearprop.repository;

import com.nearprop.entity.Coupon;
import com.nearprop.entity.SubscriptionPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    
    Optional<Coupon> findByCode(String code);
    
    Optional<Coupon> findByPermanentId(String permanentId);
    
    @Query("SELECT c FROM Coupon c WHERE c.active = true AND c.validFrom <= :now AND c.validUntil >= :now " +
           "AND (c.maxUses IS NULL OR c.currentUses < c.maxUses)")
    List<Coupon> findAllValidCoupons(LocalDateTime now);
    
    @Query("SELECT c FROM Coupon c WHERE c.active = true AND c.validFrom <= :now AND c.validUntil >= :now " +
           "AND (c.maxUses IS NULL OR c.currentUses < c.maxUses) " +
           "AND (c.subscriptionType IS NULL OR c.subscriptionType = :planType)")
    List<Coupon> findValidCouponsByPlanType(LocalDateTime now, SubscriptionPlan.PlanType planType);
    
    @Query("SELECT c FROM Coupon c WHERE c.active = true AND c.validFrom <= :now AND c.validUntil >= :now " +
           "AND (c.maxUses IS NULL OR c.currentUses < c.maxUses) " +
           "AND c.code = :code")
    Optional<Coupon> findValidCouponByCode(String code, LocalDateTime now);
    
    Page<Coupon> findByActive(boolean active, Pageable pageable);
} 