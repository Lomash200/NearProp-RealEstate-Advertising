package com.nearprop.repository;

import com.nearprop.entity.Subscription;
import com.nearprop.entity.Subscription.SubscriptionStatus;
import com.nearprop.entity.SubscriptionPlan;
import com.nearprop.entity.SubscriptionPlan.PlanType;
import com.nearprop.entity.SubscriptionPlan.PlanType;
import com.nearprop.entity.SubscriptionType;
import com.nearprop.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    @Query("""
    SELECT DISTINCT s.user
    FROM Subscription s
    WHERE s.plan.id = :planId
      AND s.status = 'ACTIVE'
      AND s.endDate > :now
""")
    List<User> findUsersByActiveSubscriptionPlan(
            @Param("planId") Long planId,
            @Param("now") LocalDateTime now
    );


    Page<Subscription> findByUserId(Long userId, Pageable pageable);
    
    List<Subscription> findByDistrictId(Long districtId);
    
    @Query("SELECT s FROM Subscription s WHERE s.user.id = :userId AND s.plan.type = :planType " +
           "AND s.status = 'ACTIVE' AND s.endDate > :now")
    List<Subscription> findActiveByUserIdAndPlanType(@Param("userId") Long userId, 
                                                  @Param("planType") PlanType planType,
                                                  @Param("now") LocalDateTime now);
    
    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.user.id = :userId AND s.plan.type = :planType " +
           "AND s.status = 'ACTIVE' AND s.endDate > CURRENT_TIMESTAMP")
    long countActiveSubscriptionsByUserIdAndPlanType(@Param("userId") Long userId, @Param("planType") PlanType planType);
    
    @Query("SELECT s FROM Subscription s WHERE s.status = :status AND s.endDate < :cutoffDate")
    List<Subscription> findExpiredSubscriptions(@Param("status") SubscriptionStatus status, 
                                             @Param("cutoffDate") LocalDateTime cutoffDate);
    
    @Query("SELECT s FROM Subscription s WHERE s.status = 'EXPIRED' AND s.contentHiddenAt IS NULL " +
           "AND s.endDate < :cutoffDate")
    List<Subscription> findSubscriptionsForContentHiding(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    @Query("SELECT s FROM Subscription s WHERE s.status = 'CONTENT_HIDDEN' AND s.contentDeletedAt IS NULL " +
           "AND s.contentHiddenAt < :cutoffDate")
    List<Subscription> findSubscriptionsForContentDeletion(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.user.id = :userId AND s.plan.type = :planType " +
           "AND s.status IN ('ACTIVE', 'PENDING_PAYMENT')")
    long countByUserIdAndPlanType(@Param("userId") Long userId, @Param("planType") PlanType planType);
    
    @Query("SELECT s FROM Subscription s WHERE s.user.id = :userId AND s.plan.type = :planType " +
           "AND s.status = 'ACTIVE' " +
           "AND (s.plan.maxProperties IS NULL OR s.plan.maxProperties = -1 OR " +
           "(SELECT COUNT(p) FROM Property p WHERE p.subscriptionId = s.id) < s.plan.maxProperties) " +
           "ORDER BY s.endDate DESC")
    List<Subscription> findSubscriptionsWithAvailablePropertySlots(@Param("userId") Long userId, 
                                                               @Param("planType") PlanType planType);
    
  /*  @Query("SELECT s FROM Subscription s WHERE s.user.id = :userId AND s.plan.type = :planType " +
           "AND s.status = 'ACTIVE' AND s.endDate > CURRENT_TIMESTAMP " +
           "AND (s.plan.maxProperties IS NULL OR s.plan.maxProperties = -1 OR " +
           "(SELECT COUNT(p) FROM Property p WHERE p.subscriptionId = s.id) < s.plan.maxProperties) " +
           "ORDER BY s.endDate DESC")
    Optional<Subscription> findActiveSubscriptionWithAvailablePropertySlots(@Param("userId") Long userId, 
                                                                        @Param("planType") PlanType planType) */

    @Query("SELECT s FROM Subscription s WHERE s.user.id = :userId AND s.plan.type = :planType " +
               "AND s.status = 'ACTIVE' AND s.endDate > CURRENT_TIMESTAMP " +
               "AND (s.plan.maxProperties IS NULL OR s.plan.maxProperties = -1 OR " +
               "(SELECT COUNT(p) FROM Property p WHERE p.subscriptionId = s.id) < s.plan.maxProperties) " +
               "ORDER BY s.endDate DESC")
        List<Subscription> findActiveSubscriptionsWithAvailablePropertySlots(@Param("userId") Long userId, 
                                                                         @Param("planType") PlanType planType);
    
    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.plan.id = :planId AND s.status = 'ACTIVE'")
    long countActiveByPlanId(@Param("planId") Long planId);
    
    @Query("SELECT COUNT(p) FROM Property p WHERE p.subscriptionId = :subscriptionId")
    long countPropertiesBySubscriptionId(@Param("subscriptionId") Long subscriptionId);
    
    List<Subscription> findByStatusAndEndDateBetween(SubscriptionStatus status, LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.user.id = :userId")
    Long countByUserId(Long userId);
    
    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.user.id = :userId AND s.status = 'ACTIVE'")
    Long countByUserIdAndStatusActive(Long userId);
    
    List<Subscription> findByUserIdIn(List<Long> userIds);




    @Query("""
    SELECT COUNT(s) > 0
    FROM Subscription s
    WHERE s.user.id = :userId
      AND s.plan.type = :planType 
      AND s.status = 'ACTIVE'
      AND s.endDate > :now
""")
    boolean existsActiveByUserIdAndPlanType(
            @Param("userId") Long userId,
            @Param("planType") SubscriptionPlan.PlanType planType, // Yahan 'planType' hai
            @Param("now") LocalDateTime now
    );



} 
